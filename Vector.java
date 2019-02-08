class Vector{

    public float vx, vy, vz;

    public Vector(float x, float y, float z){
	vx = x;
	vy = y;
	vz = z;
    }

    //PRE: 2 vectors of length 3
    //POST: return the scalar of the two vectors
    public static float dot(Vector v1, Vector v2){
	float scalar = (v1.vx * v2.vx) + (v1.vy * v2.vy) + (v1.vz * v2.vz);
	return scalar;
    }

    //PRE: 2 vectors of length 3
    //POST: returns the vector that is the cross product
    public static Vector cross(Vector v1, Vector v2){
	Vector returnVector = new Vector(0.0f, 0.0f, 0.0f);
	returnVector.vx = (v1.vy * v2.vz) - (v1.vz * v2.vy);
	returnVector.vy = -1 * ((v1.vz * v2.vx) - (v1.vx * v2.vz));
	returnVector.vz = (v1.vx * v2.vy) - (v1.vy * v2.vx);
	return returnVector;
    }

    //PRE: 6 values x,y,z
    //POST: returns the vector of the 6 points
    public static Vector displacementVector(float x1, float y1, float z1, 
					    float x2, float y2, float z2){
	Vector returnVector = new Vector(0.0f, 0.0f, 0.0f);
	returnVector.vx = x2 - x1;
	returnVector.vy = y2 - y1;
	returnVector.vz = z2 - z1;
	return returnVector;
    }

    //PRE: a matrix
    //poST: returns the converted matrix into a vector
    public static Vector matrixToVector(Matrix matrix1){
	Vector returnVector = new Vector(0.0f, 0.0f, 0.0f);
	returnVector.vx = matrix1.matrix[0][3];
	returnVector.vy = matrix1.matrix[1][3];
	returnVector.vz = matrix1.matrix[2][3];
	return returnVector;
    }

    //PRE: 2 vectors of length 3
    //POST: returns a vector that is the subraction between two vectors
    public static Vector subtract(Vector v1, Vector v2){
	Vector returnVector = new Vector(0.0f, 0.0f, 0.0f);
	returnVector.vx = v1.vx - v2.vx;
	returnVector.vy = v1.vy - v2.vy;
	returnVector.vz = v1.vz - v2.vz;
	return returnVector;
    }

    //Overload the toString operade to output <x,y,z>
    public String toString(){
	String returnString = "";
	returnString = returnString + "<" + vx + ", " + vy + ", " + vz + ">";
	return returnString;
    }

    public static void main(String [] args){
	Vector a = new Vector(1.0f, 2.0f, 0.0f);
	Vector b = new Vector(3.0f, 2.0f, 0.0f);
	System.out.println(cross(a, b).toString());
	System.out.println(dot(a, b));
	System.out.println(subtract(a, b).toString());
    }

}