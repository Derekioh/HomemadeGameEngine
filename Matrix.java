//static: all objects of the same things, aka Matrix, are gonna to be the same

//Matrix r = Matrix.multiply( Matrix.rotationZ(45), Matrix.translation(10, 0, 0) );

import java.nio.FloatBuffer;
import com.badlogic.jglfw.utils.Memory;

class Matrix{

    private final int BYTES_PER_FLOAT = 4;
    private static final int MATRIX_SIZE = 4;
    public float[][] matrix = new float[MATRIX_SIZE][MATRIX_SIZE];

    //PRE: void
    //POST: creates a matrix that stores 1 in the 00,11,22,33 position
    public static Matrix identity(){
	Matrix returnMatrix = new Matrix();
	returnMatrix.matrix[0][0] = 1;
	returnMatrix.matrix[1][1] = 1;
	returnMatrix.matrix[2][2] = 1;
	returnMatrix.matrix[3][3] = 1;
	return returnMatrix;
    }

    //PRE: a vector
    //POST: returns a matrix with the x,y,z, on the end of the matrix
    public static Matrix vectorToMatrix(Vector vector1){
	Matrix returnMatrix = new Matrix();
	returnMatrix.matrix[0][3] = vector1.vx;
	returnMatrix.matrix[1][3] = vector1.vy;
	returnMatrix.matrix[2][3] = vector1.vz;
	returnMatrix.matrix[3][3] = 1;
	return returnMatrix;
    }

    //PRE: 3 floats
    //POST: overloads vectorToMatrix
    public static Matrix vectorToMatrix(float x, float y, float z){
	Matrix returnMatrix = new Matrix();
	returnMatrix.matrix[0][3] = x;
	returnMatrix.matrix[1][3] = y;	
	returnMatrix.matrix[2][3] = z;
	returnMatrix.matrix[3][3] = 1;
	return returnMatrix;
    }

    //PRE: 6 floats for vertex 1 and 2 and the model matrix of the current clone
    //POST: returns the displaced matrix
    public static Matrix getDisplacementMatrix(float x1, float y1, float z1,
					       float x2, float y2, float z2,
					       Matrix modelMatrix){
	Matrix returnMatrix = new Matrix();
	Matrix vertex1_matrix, vertex2_matrix;

	vertex1_matrix = Matrix.vectorToMatrix(x1, y1, z1);
	vertex2_matrix = Matrix.vectorToMatrix(x2, y2, z2);

	//matrix * Vector1 - matrix * Vector2
	returnMatrix = Matrix.subtract(Matrix.multiply(modelMatrix, vertex2_matrix),
						  Matrix.multiply(modelMatrix, vertex1_matrix));
	return returnMatrix;
    }

    //PRE: input the x,y,z factor to scale by
    //POST: create a matrix with this scalar factor
    public static Matrix scale(float x, float y, float z){
	Matrix returnMatrix = new Matrix();
	returnMatrix.matrix[0][0] = x;
	returnMatrix.matrix[1][1] = y;
	returnMatrix.matrix[2][2] = z;
	returnMatrix.matrix[3][3] = 1;
	return returnMatrix;
    }

    //PRE: input the theta value to rotate by, and the x,y,z coordinate
    //POST: create the rotation matrix by the opengl documentation
    public static Matrix rotatef(float theta, float x, float y, float z){
	Matrix returnMatrix = new Matrix();
	float c = (float)Math.cos(theta);
	float s = (float)Math.sin(theta);
	returnMatrix.matrix[0][0] = (x * x) * (1 - c) + c;
	returnMatrix.matrix[0][1] = (x * y) * (1 - c) - z * s;
	returnMatrix.matrix[0][2] = (x * z) * (1 - c) + y * s;

	returnMatrix.matrix[1][0] = (y * x) * (1 - c) + z * s;
	returnMatrix.matrix[1][1] = (y * y) * (1 - c) + c;
	returnMatrix.matrix[1][2] = (y * z) * (1 - c) - x * s;

	returnMatrix.matrix[2][0] = (x * z) * (1 - c) - y * s;
	returnMatrix.matrix[2][1] = (y * z) * (1 - c) + x * s;
	returnMatrix.matrix[2][2] = (z * z) * (1 - c) + c;

	returnMatrix.matrix[3][3] = 1;

	return returnMatrix;
    }

    //PRE: accepts a radian
    //POST: returns the rotation matrix
    public static Matrix rotateZ(float theta){
    	Matrix returnMatrix = new Matrix();
    	returnMatrix.matrix[0][0] = (float)Math.cos(theta);
    	returnMatrix.matrix[0][1] = (float)Math.sin(theta) * -1;
    	returnMatrix.matrix[1][0] = (float)Math.sin(theta);
    	returnMatrix.matrix[1][1] = (float)Math.cos(theta);
    	returnMatrix.matrix[2][2] = 1;
    	returnMatrix.matrix[3][3] = 1;
    	return returnMatrix;
    }

    //PRE: accepts a radian
    //POST: returns the rotation matrix
    public static Matrix rotateX(float theta){
    	Matrix returnMatrix = new Matrix();
    	returnMatrix.matrix[0][0] = 1;
    	returnMatrix.matrix[1][1] = (float)Math.cos(theta);
    	returnMatrix.matrix[1][2] = (float)Math.sin(theta) *-1;
    	returnMatrix.matrix[2][1] = (float)Math.sin(theta);
	returnMatrix.matrix[2][2] = (float)Math.cos(theta);
    	returnMatrix.matrix[3][3] = 1;
    	return returnMatrix;
    }

    //PRE: accepts a radian
    //POST: returns the rotation matrix
    public static Matrix rotateY(float theta){
	Matrix returnMatrix = new Matrix();
	returnMatrix.matrix[0][0] = (float)Math.cos(theta);
    	returnMatrix.matrix[0][2] = (float)Math.sin(theta) *-1;
    	returnMatrix.matrix[1][1] = 1;
    	returnMatrix.matrix[2][0] = (float)Math.sin(theta);
    	returnMatrix.matrix[2][2] = (float)Math.cos(theta);
    	returnMatrix.matrix[3][3] = 1;
    	return returnMatrix;
    }

    public static Matrix translate(float dx, float dy, float dz){
	Matrix returnMatrix = new Matrix();
	returnMatrix.matrix[0][0] = 1;
	returnMatrix.matrix[0][3] = dx;
	returnMatrix.matrix[1][1] = 1;
	returnMatrix.matrix[1][3] = dy;
	returnMatrix.matrix[2][2] = 1;
	returnMatrix.matrix[2][3] = dz;
	returnMatrix.matrix[3][3] = 1;
	return returnMatrix;
    }

    //PRE: two matrix of size 4x4
    //POST: return the multiplication of the two
    //NOTE: only works if the bottom row is 0,0,0,1
    public static Matrix multiply (Matrix matrix1, Matrix matrix2){
	Matrix returnMatrix = new Matrix();
	for(int row = 0; row < MATRIX_SIZE; row++){
	    for(int col = 0; col < MATRIX_SIZE; col++){
		returnMatrix.matrix[row][col] = 0.0f;
		for(int index = 0; index < MATRIX_SIZE; index++){
		    returnMatrix.matrix[row][col]+= matrix1.matrix[row][index] * matrix2.matrix[index][col];
		}
	    }
	}
	// System.out.println("MULTIPLY");
	// System.out.println(returnMatrix);
	return returnMatrix;
    }

    //PRE: two matrix of size 4x4
    //POST: returns the subtraction of the two
    public static Matrix subtract(Matrix matrix1, Matrix matrix2){
	Matrix returnMatrix = new Matrix();
	for(int row = 0; row < MATRIX_SIZE; row++){
	    for(int col = 0; col < MATRIX_SIZE; col++){
		returnMatrix.matrix[row][col] = matrix1.matrix[row][col] - matrix2.matrix[row][col];
	    }
	}
	return returnMatrix;
    }

    //Pre: all floats of the view
    //POST: returns a matrix of the glOrtho
    public static Matrix ortho (float left, float right, float bottom,
				float top, float nearVal, float farVal){
	Matrix returnMatrix = new Matrix();
	returnMatrix.matrix[0][0] = 2 / (right - left);
	returnMatrix.matrix[0][3] = -((right + left) / (right - left));
	returnMatrix.matrix[1][1] = 2 / (top - bottom);
	returnMatrix.matrix[1][3] = -((top + bottom) / (top - bottom));
	returnMatrix.matrix[2][2] = -2 / (farVal - nearVal);
	returnMatrix.matrix[2][3] = -((farVal + nearVal) / (farVal - nearVal));
	returnMatrix.matrix[3][3] = 1;
	return returnMatrix;
    }

    //PRE: the field of view in the y, the aspect ratio, the zNear and zFar
    //POST: returns the matrix of the projections
    public static Matrix perspective(float fovy,float aspect,
				     float zNear, float zFar){
	Matrix returnMatrix = new Matrix();
	returnMatrix.matrix[0][0] = (1 / (float)Math.tan(fovy / 2)) / aspect;
	returnMatrix.matrix[1][1] = (1 / (float)Math.tan(fovy / 2));
	returnMatrix.matrix[2][2] = (zFar + zNear) / (zNear - zFar);
	returnMatrix.matrix[2][3] = (2 * zFar * zNear) / (zNear - zFar);
	returnMatrix.matrix[3][2] = -1;
	return returnMatrix;
    }

    //PRE: void
    //POST: returns the data of the array
    public final FloatBuffer data() {
	float[] data = new float[16];
	int counter = 0;
	for (int row = 0; row < matrix.length; row++){
	    for (int col = 0; col < matrix[0].length; col++){
		data[counter] = matrix[row][col];
		counter += 1;
	    }
	}
	int bufferSize = data.length * BYTES_PER_FLOAT;
        FloatBuffer buffer = Memory.malloc(bufferSize).asFloatBuffer();
        buffer.put(data);
        return buffer;
    }

    //PRE: void
    //POST: normalizes the matrix
    public final Matrix normalMatrix() {
        final Matrix normal = identity();
        final float determinantReciprocal = 1.0f
             / (matrix[0][0] * matrix[1][1] * matrix[2][2]
             + matrix[1][0] * matrix[2][1] * matrix[0][2]
             + matrix[2][0] * matrix[0][1] * matrix[1][2]
             - matrix[0][0] * matrix[2][1] * matrix[1][2]
             - matrix[2][0] * matrix[1][1] * matrix[0][2]
             - matrix[1][0] * matrix[0][1] * matrix[2][2]);
        normal.matrix[0][0] = determinantReciprocal
            * (matrix[1][1] * matrix[2][2] - matrix[1][2] * matrix[2][1]);
        normal.matrix[0][1] = determinantReciprocal
            * (matrix[1][2] * matrix[2][0] - matrix[1][0] * matrix[2][2]);
        normal.matrix[0][2] = determinantReciprocal
            * (matrix[1][0] * matrix[2][1] - matrix[1][1] * matrix[2][0]);
        normal.matrix[1][0] = determinantReciprocal
            * (matrix[0][2] * matrix[2][1] - matrix[0][1] * matrix[2][2]);
        normal.matrix[1][1] = determinantReciprocal
            * (matrix[0][0] * matrix[2][2] - matrix[0][2] * matrix[2][0]);
        normal.matrix[1][2] = determinantReciprocal
            * (matrix[0][1] * matrix[2][0] - matrix[0][0] * matrix[2][1]);
        normal.matrix[2][0] = determinantReciprocal
            * (matrix[0][1] * matrix[1][2] - matrix[0][2] * matrix[1][1]);
        normal.matrix[2][1] = determinantReciprocal
            * (matrix[0][2] * matrix[1][0] - matrix[0][0] * matrix[1][2]);
        normal.matrix[2][2] = determinantReciprocal
            * (matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0]);
        return normal;
    }

    //Pre: overload thee toString
    //POST: print out the matrix
    public String toString(){
	String line = "";
	String returnString = "";
	for (int row = 0; row < MATRIX_SIZE; row++){
	    line = "[";
	    for (int col = 0; col < MATRIX_SIZE; col++){
		line += matrix[row][col];
	    }
	    line += "]\n";
	    returnString += line;
	}
	return returnString;
    }

    // public static void main(String [] args){
    // 	Matrix point = new Matrix();
    // 	point.matrix[0][3] = 2;
    // 	point.matrix[1][3] = 2;
    // 	point.matrix[2][3] = 0;
    // 	point.matrix[3][3] = 1;

    // 	Matrix ortho = new Matrix();
    // 	ortho = Matrix.ortho(-640 / 480, 640 / 480, -1.0f, 1.0f, 1.0f, -1.0f);
	
    // 	Matrix multiply = new Matrix();
    //     multiply = Matrix.multiply(ortho, point);
	
    // 	System.out.println(multiply);
    // }

}