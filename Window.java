import java.nio.IntBuffer;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.util.Scanner;
import static com.badlogic.jglfw.Glfw.*;
import static com.badlogic.jglfw.gl.GL.*;
import com.badlogic.jglfw.utils.Memory;

public class Window{

    private static int START_WIDTH = 640;
    private static int START_HEIGHT = 480;
    private static final int BYTES_PER_INT = 4;
    private static int VERTICAL_SYNC = 1;
    private long window;

    public static int vertexId;                  // shader vertex handle
    public static int normalId;                  // shader normal handle
    public static int texCoordId;
    public static Matrix projectionMatrix;

    //PRE: string that is used for the title of the program
    //POST: creates window
    public Window(){
	// Initialize GLFW
	if (!glfwInit()) {
	    System.out.println("Failed to initialize GLFW");
	    System.exit(-1);
	}
	glfwWindowHint(GLFW_DEPTH_BITS, 16); // this is needed on virtualbox

	// Open a widnow and create its OpenGl context
	window = glfwCreateWindow(START_WIDTH, START_HEIGHT, "Test App", 0, 0);
	if (window == 0) {
	    throw new RuntimeException("Failed to open GLFW window");
	}
	glfwMakeContextCurrent(window);

	// Enable vertical sync (on cards that support it)
	glfwSwapInterval(VERTICAL_SYNC);

	load_shaders();

	// enable depth testing
	glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
	glEnable(GL_DEPTH_TEST);
	glDepthFunc(GL_LESS);
    }

    private String readFile(String fileName){

	String fileString = null;
	Scanner scanner = null;

	try {
	    File file = new File(fileName);
	    StringBuilder fileContents = new StringBuilder((int)file.length());
	    scanner = new Scanner(file);
	    String lineSeparator = System.getProperty("line.separator");

	    while(scanner.hasNextLine()) {        
		fileContents.append(scanner.nextLine() + lineSeparator);
	    }
	    fileString = fileContents.toString();
	}catch(Exception e){
	    
	    System.out.println(e);
	    System.exit(7);

	}finally {
	    if (scanner != null){
		scanner.close();
	    }
	}
	return fileString;
    }


    public void load_shaders(){

	int vertexShaderId;
	int fragmentShaderId;
	String vertexShaderSource;     // vertex shader source code
	String fragmentShaderSource;   // fragment shader source code
	int programId;                 // shader program handle
	IntBuffer resultBuffer;        // shader compile and link result


	// Load and compile vertex shader
	vertexShaderId = glCreateShader(GL_VERTEX_SHADER);
	vertexShaderSource = readFile("transform.vert");
	glShaderSource(vertexShaderId, vertexShaderSource);
	glCompileShader(vertexShaderId);

	// Verify vertex shader compiled
	resultBuffer = Memory.malloc(BYTES_PER_INT).asIntBuffer();
	glGetShaderiv(vertexShaderId, GL_COMPILE_STATUS, resultBuffer, 0);
	if (resultBuffer.get(0) == GL_FALSE) {
	    System.out.println("Error compiling vertex shader");
	    System.out.println(glGetShaderInfoLog(vertexShaderId));
	}

	// Load and compile fragment shader
	fragmentShaderId = glCreateShader(GL_FRAGMENT_SHADER);
	fragmentShaderSource = readFile("transform.frag");
	glShaderSource(fragmentShaderId, fragmentShaderSource);
	glCompileShader(fragmentShaderId);

	// Verify fragment shader compiled
	resultBuffer = Memory.malloc(BYTES_PER_INT).asIntBuffer();
	glGetShaderiv(fragmentShaderId, GL_COMPILE_STATUS, resultBuffer, 0);
	if (resultBuffer.get(0) == GL_FALSE) {
	    System.out.println("Error compiling fragment shader");
	    System.out.println(glGetShaderInfoLog(vertexShaderId));
	}

	// Link shaders
	programId = glCreateProgram();
	glAttachShader(programId, vertexShaderId);
	glAttachShader(programId, fragmentShaderId);
	glLinkProgram(programId);

	// Verify shaders linked
	resultBuffer = Memory.malloc(BYTES_PER_INT).asIntBuffer();
	glGetProgramiv(programId, GL_LINK_STATUS, resultBuffer, 0);
	if (resultBuffer.get(0) == GL_FALSE) {
	    System.out.println("Error linking shaders.");
	    System.out.println(glGetProgramInfoLog(vertexShaderId));
	}

	// Set shader
	glUseProgram(programId);

	// Get shader uniform variable ids
	Model.modelViewProjectionMatrixId = glGetUniformLocation(programId, "model_view_projection_matrix4f");
        Model.modelViewMatrixId = glGetUniformLocation(programId, "model_view_matrix4f");
        Model.normalMatrixId = glGetUniformLocation(programId, "normal_matrix4f");
	Model.ambientId = glGetUniformLocation(programId, "ambient_color_4f");
	Model.diffuseId = glGetUniformLocation(programId, "diffuse_color_4f");
	Model.specularId = glGetUniformLocation(programId, "specular_color_4f");
	Model.specularCoefficientId = glGetUniformLocation(programId, "specular_coefficient_1f");
	Model.lightPositionId = glGetUniformLocation(programId, "light_position_3f");
	Model.lightColorId = glGetUniformLocation(programId, "light_color_4f");
	Model.attenuationFactorId = glGetUniformLocation(programId, "attenuation_factor_1f");
	Model.textureSamplerId = glGetUniformLocation(programId, "textureSampler");
	//Model.bumpSamplerId = glGetUniformLocation(programId, "bumpSampler");

	// Get shader attribute variable ids
	vertexId = glGetAttribLocation(programId, "vertex_3f");
	normalId = glGetAttribLocation(programId, "normal_3f");
	texCoordId = glGetAttribLocation(programId, "tex_coord_2f");

    }

    //PRE: takes the ratio of the window
    //POST: updates the stuff
    public void update(float ratio){
	projectionMatrix = Matrix.perspective(27.0f, ratio, 0.1f, 10.0f);
	
    }

    //PRE: void
    //POST: updates the events and buffers of the window
    public void clear(){

	// Disable attribute arrays
	glDisableVertexAttribArray(vertexId);
	glDisableVertexAttribArray(normalId);
	glDisableVertexAttribArray(texCoordId);

	// Poll for events
	glfwPollEvents();

	// Swap buffers
	glfwSwapBuffers(window);

    }
    
    //PRE: void
    //POST: returns if the window should close
    public boolean should_close(){
	return glfwWindowShouldClose(window);
    }
    
    //PRE: void
    //POST: returns the window width
    public int get_width(){
	return glfwGetWindowWidth(window);
    }

    //PRE: void
    //POST: returns the window height
    public int get_height(){
	return glfwGetWindowHeight(window);
    }

    //PRE: takes the keybaord key to check
    //POST: returns the keystate of the key
    public boolean get_key(int key){
	return glfwGetKey(window, key);
    }

    //PRE: void
    //POST: cleanup the window glfw stuff
    public void cleanup(){
	//window.cleanup();
	// Close OpenGL window and terminate GLFW
	glfwDestroyWindow(window);
	glfwTerminate();
    }

}