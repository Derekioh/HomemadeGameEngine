
class Camera{
    
    public static Matrix viewMatrix;
    private Matrix viewMatrixTranslate;
    private Matrix viewMatrixRotate;
   
    public Camera(){
    	viewMatrix = Matrix.identity();
    	// viewMatrixTranslate = Matrix.identity();
        // viewMatrixRotate = Matrix.identity();
    }

    //PRE: intakes the x,y,z movement you want to translate by
    //POST: moves the viewMatrix by the x,y,z amount
    public void translate(float x, float y, float z){
    	// viewMatrixTranslate = Matrix.multiply(Matrix.translate(x,y,z), viewMatrixTranslate);
	viewMatrix = Matrix.multiply(Matrix.translate(x,y,z), viewMatrix);
    }
    //PRE: the x,y,z Theta values to rotate by
    //POST: rotate by x,y,z theta
    public void rotate(float x, float y, float z){
    	// viewMatrixRotate = Matrix.multiply(Matrix.rotateX(x), viewMatrixRotate);
    	// viewMatrixRotate = Matrix.multiply(Matrix.rotateY(y), viewMatrixRotate);
    	// viewMatrixRotate = Matrix.multiply(Matrix.rotateZ(z), viewMatrixRotate);
	viewMatrix = Matrix.multiply(Matrix.rotateX(x), viewMatrix);
    	viewMatrix = Matrix.multiply(Matrix.rotateY(y), viewMatrix);
    	viewMatrix = Matrix.multiply(Matrix.rotateZ(z), viewMatrix);
    }

    //PRE: void
    //POST: updates the view matrix
    public void update(){
    	//viewMatrix = Matrix.multiply(viewMatrixTranslate, viewMatrixRotate);
    }

}