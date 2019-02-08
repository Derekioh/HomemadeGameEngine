import static com.badlogic.jglfw.Glfw.*;
import static com.badlogic.jglfw.gl.GL.*;
import com.badlogic.jglfw.utils.Memory;

class Test{

    private Window window;
    private Model model;
    private Clone clone1;
    private Clone clone2;
    private Camera camera;
    private Light light;

    private boolean cloneMoved = false;

    public Test(){
	window = new Window();
	
	model = new Model();

	model.load("crate.obj");
	
	clone1 = new Clone(model);
	clone2 = new Clone(model);

	camera = new Camera();

	light = new Light();

	light.activate();

	main_loop();
	
	window.cleanup();
	model.cleanup();

    }
    
    public void main_loop(){

	
	float x_rot = 0.0f;
	float y_rot = 0.0f;
	float x_mov = 0.0f;
	float z_mov = 0.0f;
	float delta_mov = 0.01f;
	float delta_rot = 0.01f;
	float ratio;
	int width, height;
    
	clone1.translate(1.0f, 0.0f, -2.0f);
	clone2.translate(-1.0f, 0.0f, -2.0f);
    

	do{
	    
	    if (clone1.collide(clone2)){
		System.out.println("Collided");
	    }

	    if (window.get_key(GLFW_KEY_LCTRL)){
		//Handle user input
		if (window.get_key(GLFW_KEY_UP)) {
		    camera.rotate(-delta_rot, 0.0f, 0.0f);
		}
		if (window.get_key(GLFW_KEY_DOWN)) {
		    camera.rotate(delta_rot, 0.0f, 0.0f);
		}
		if (window.get_key(GLFW_KEY_LEFT)) {
		    camera.rotate(0.0f, delta_rot, 0.0f);
		}
		if (window.get_key(GLFW_KEY_RIGHT)) {
		    camera.rotate(0.0f, -delta_rot, 0.0f);
		}

		if (window.get_key(GLFW_KEY_W)) {
		    camera.translate(0.0f, 0.0f, delta_mov);
		}
		if (window.get_key(GLFW_KEY_S)) {
		    camera.translate(0.0f, 0.0f, -delta_mov);
		}
		if (window.get_key(GLFW_KEY_A)) {
		    camera.translate(delta_mov, 0.0f, 0.0f);
		}
		if (window.get_key(GLFW_KEY_D)) {
		    camera.translate(-delta_mov, 0.0f, 0.0f);
		}
	    }else{
		//Handle user input
		if (window.get_key(GLFW_KEY_UP)) {
		    clone1.rotate(delta_rot, 0.0f, 0.0f);
		}
		if (window.get_key(GLFW_KEY_DOWN)) {
		    clone1.rotate(-delta_rot, 0.0f, 0.0f);
		}
		if (window.get_key(GLFW_KEY_LEFT)) {
		    clone1.rotate(0.0f, delta_rot, 0.0f);
		}
		if (window.get_key(GLFW_KEY_RIGHT)) {
		    clone1.rotate(0.0f, -delta_rot, 0.0f);
		}

		if (window.get_key(GLFW_KEY_W)) {
		    clone1.translate(0.0f, 0.0f, delta_mov);
		}
		if (window.get_key(GLFW_KEY_S)) {
		    clone1.translate(0.0f, 0.0f, -delta_mov);
		}
		if (window.get_key(GLFW_KEY_A)) {
		    clone1.translate(-delta_mov, 0.0f, 0.0f);
		}
		if (window.get_key(GLFW_KEY_D)) {
		    clone1.translate(delta_mov, 0.0f, 0.0f);
		}
	    }
	    // Get window size (may be different than the requested size)
	    width = window.get_width();
	    height = window.get_height();

	    // Set the rendering viewport location and dimenstions
	    height = height > 0 ? height : 1;
	    glViewport(0, 0, width, height);

	    // Clear color buffer to black
	    glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
	    
	    // Select and setup the projection matrix
	    ratio = width / (float) height;

	    window.update(ratio);

	    // Rotate view

	    //clone1.rotate(x_rot, delta_rot, 0.0f, 0.0f);
	    //clone1.rotate(y_rot, 0.0f, delta_rot, 0.0f);
	  
	    clone1.draw();
	    clone2.draw();
	    camera.update();
	    window.clear();
	
	}// Check if the ESC key was pressed or the window was closed
	while (!window.get_key(GLFW_KEY_ESC) &&
	       !window.should_close());

    }

    public static void main(String[] args) throws Exception {
	
	Test test = new Test();
    }

}