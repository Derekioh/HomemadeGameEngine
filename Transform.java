//========================================================================
// This is a small test application for GLFW.
// The program opens a window (640x480),
// and renders a texture mapped triangle.
//========================================================================

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ByteBuffer;
import java.util.Scanner;
import java.io.IOException;
import java.io.File;
import static com.badlogic.jglfw.Glfw.*;
import static com.badlogic.jglfw.gl.GL.*;
import com.badlogic.jglfw.utils.Memory;

public class Transform {
    static final int BYTES_PER_INT = 4;
    static final int BYTES_PER_FLOAT = 4;

    private static String readFile(String fileName) throws IOException {
        File file = new File(fileName);
        StringBuilder fileContents = new StringBuilder((int)file.length());
        Scanner scanner = new Scanner(file);
        String lineSeparator = System.getProperty("line.separator");
        try {
            while(scanner.hasNextLine()) {
                fileContents.append(scanner.nextLine() + lineSeparator);
            }
        } finally {
            scanner.close();
        }
        return fileContents.toString();
    }

    public static void main (String[] args) throws Exception {
        long window;                  // GLFW window handle
        int width, height;            // Window width and height
        float ratio;                  // Window aspect ratio
        float xRot = 0.0f;            // X rotation scaling factor
        float yRot = 0.0f;            // Y rotation scaling factor
        float deltaRot = (float) Math.PI / 180.0f; // Amount rotation changes on key press

        int vertexShaderId;              // vertex shader handle
        int fragmentShaderId;            // fragment shader handle
        String vertexShaderSource;       // vertex shader source code
        String fragmentShaderSource;     // fragment shader source code
        int programId;                   // shader program handle
        IntBuffer resultBuffer;          // shader compile and link result
        int modelViewProjectionMatrixId; // mvp matrix handle
        int modelViewMatrixId;           // mv matrix handle
        int normalMatrixId;              // normal matrix handle
        int ambientId;                   // shader material ambient handle
        int diffuseId;                   // shader material diffuse handle
        int specularId;                  // shader material specular handle
        int specularCoefficientId;       // shader material specular handle
        int lightPositionId;             // shader light position handle
        int lightColorId;                // shader light color handle
        int attenuationFactorId;         // shader light attenuation handle
        int vertexId;                    // shader vertex handle
        int normalId;                    // shader normal handle
        int texCoordId;                  // shader texture coordinate handle
        int textureSamplerId;            // shader texture sampler handle

        Matrix modelViewProjectionMatrix; // transfrom vertices for rendering
        Matrix modelViewMatrix;           // transform vertices for lighting
        Matrix normalMatrix;              // transform normals for lighting
        Matrix projectionMatrix;          // projection transformation
        Matrix viewMatrix;                // view transformation
        Matrix modelMatrix;               // model transformation

        int numberOfVertices = 3; // Number of vertices in buffer
        float vertexArray[] = {-0.6f, -0.4f, 0.0f, // vertex coordinates
                               0.6f, -0.4f, 0.0f,
                               0.0f, 0.6f, 0.0f};
        float normalArray[] = {0.0f, 0.0f, 1.0f,   // vertex normals
                               0.0f, 0.0f, 1.0f,
                               0.0f, 0.0f, 1.0f};
        float textureCoordArray[] = {0.0f, 0.0f,   // vertex texture coords
                                     1.0f, 0.0f,
                                     0.5f, 1.0f};
        final byte MAX_BYTE = (byte) 255; // largest unsiged byte
        byte textureDataArray[] = {MAX_BYTE, 0,        0,        MAX_BYTE,
                                   0,        MAX_BYTE, 0,        MAX_BYTE,
                                   0,        0,        MAX_BYTE, MAX_BYTE,
                                   MAX_BYTE, MAX_BYTE, MAX_BYTE, MAX_BYTE,
                                   0,        MAX_BYTE, MAX_BYTE, MAX_BYTE,
                                   MAX_BYTE, 0,        MAX_BYTE, MAX_BYTE,
                                   MAX_BYTE, MAX_BYTE, 0,        MAX_BYTE,
                                   0,        0,        0,        MAX_BYTE};
        int bufferSize; // Number of floats in vertex buffer objects
        FloatBuffer vertexBuffer; // vertex coordinates buffer
        FloatBuffer normalBuffer; // vertex normals buffer
        FloatBuffer textureCoordBuffer; // vertex texture coordinates buffer
        ByteBuffer textureDataBuffer; // vertex texture data buffer
        IntBuffer vertexBufferId; // vertex buffer handle
        IntBuffer normalBufferId; // normal buffer handle
        IntBuffer textureCoordBufferId; // texture coordinate buffer handle
        IntBuffer textureDataBufferId; // texture coordinate buffer handle

        // Initialize GLFW
        if (!glfwInit()) {
            System.out.println("Failed to initialize GLFW");
            System.exit(-1);
        }
        glfwWindowHint(GLFW_DEPTH_BITS, 16); // this is needed on virtualbox

        // Open a widnow and create its OpenGl context
        window = glfwCreateWindow(640, 480, "", 0, 0);
        if (window == 0) {
            throw new RuntimeException("Failed to open GLFW window");
        }
        glfwMakeContextCurrent(window);

        // Enable vertical sync (on cards that support it)
        glfwSwapInterval(1);

        // Load texture
        textureDataBuffer = Memory.malloc(textureDataArray.length);
        textureDataBufferId = Memory.malloc(BYTES_PER_INT).asIntBuffer();
        textureDataBuffer.put(textureDataArray);
        glGenTextures(1, textureDataBufferId, 0);
        glBindTexture(GL_TEXTURE_2D, textureDataBufferId.get(0));
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, 4, 2, 0, GL_RGBA, GL_UNSIGNED_BYTE, textureDataBuffer, 0);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

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
        modelViewProjectionMatrixId = glGetUniformLocation(programId, "model_view_projection_matrix4f");
        modelViewMatrixId = glGetUniformLocation(programId, "model_view_matrix4f");
        normalMatrixId = glGetUniformLocation(programId, "normal_matrix4f");
        ambientId = glGetUniformLocation(programId, "ambient_color_4f");
        diffuseId = glGetUniformLocation(programId, "diffuse_color_4f");
        specularId = glGetUniformLocation(programId, "specular_color_4f");
        specularCoefficientId = glGetUniformLocation(programId, "specular_coefficient_1f");
        lightPositionId = glGetUniformLocation(programId, "light_position_3f");
        lightColorId = glGetUniformLocation(programId, "light_color_4f");
        attenuationFactorId = glGetUniformLocation(programId, "attenuation_factor_1f");
        textureSamplerId = glGetUniformLocation(programId, "textureSampler");

        // Get shader attribute variable ids
        vertexId = glGetAttribLocation(programId, "vertex_3f");
        normalId = glGetAttribLocation(programId, "normal_3f");
        texCoordId = glGetAttribLocation(programId, "tex_coord_2f");

        // Create vertex buffer
        bufferSize = vertexArray.length * BYTES_PER_FLOAT;
        vertexBuffer = Memory.malloc(bufferSize).asFloatBuffer();
        vertexBufferId = Memory.malloc(BYTES_PER_INT).asIntBuffer();
        vertexBuffer.put(vertexArray);
        glGenBuffers(1, vertexBufferId, 0);
        glBindBuffer(GL_ARRAY_BUFFER, vertexBufferId.get(0));
        glBufferData(GL_ARRAY_BUFFER, bufferSize, vertexBuffer, 0, GL_STATIC_DRAW);

        // Create normal buffer
        bufferSize = normalArray.length * BYTES_PER_FLOAT;
        normalBuffer = Memory.malloc(bufferSize).asFloatBuffer();
        normalBufferId = Memory.malloc(BYTES_PER_INT).asIntBuffer();
        normalBuffer.put(normalArray);
        glGenBuffers(1, normalBufferId, 0);
        glBindBuffer(GL_ARRAY_BUFFER, normalBufferId.get(0));
        glBufferData(GL_ARRAY_BUFFER, bufferSize, normalBuffer, 0, GL_STATIC_DRAW);

        // Create texture coordinate buffer
        bufferSize = textureCoordArray.length * BYTES_PER_FLOAT;
        textureCoordBuffer = Memory.malloc(bufferSize).asFloatBuffer();
        textureCoordBufferId = Memory.malloc(BYTES_PER_INT).asIntBuffer();
        textureCoordBuffer.put(textureCoordArray);
        glGenBuffers(1, textureCoordBufferId, 0);
        glBindBuffer(GL_ARRAY_BUFFER, textureCoordBufferId.get(0));
        glBufferData(GL_ARRAY_BUFFER, bufferSize, textureCoordBuffer, 0, GL_STATIC_DRAW);

        // enable depth testing
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);

        do {
            // Handle user input
            if (glfwGetKey(window, GLFW_KEY_UP)) {
                xRot += deltaRot;
            }
            if (glfwGetKey(window, GLFW_KEY_DOWN)) {
                xRot -= deltaRot;
            }
            if (glfwGetKey(window, GLFW_KEY_LEFT)) {
                yRot += deltaRot;
            }
            if (glfwGetKey(window, GLFW_KEY_RIGHT)) {
                yRot -= deltaRot;
            }

            // Get window size (may be different than the requested size)
            width = glfwGetWindowWidth(window);
            height = glfwGetWindowHeight(window);

            // Set the rendering viewport location and dimenstions
            height = height > 0 ? height : 1;
            glViewport(0, 0, width, height);

            // Clear color buffer to black
            glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);

            // Set Model, View, Projection and Normal matrices
            ratio = width / (float) height;
            // projectionMatrix = Matrix.orthographic(-ratio, ratio, -1.0f, 1.0f, 0.1f, 10.0f);
            projectionMatrix = Matrix.perspective(27.0f, ratio, 0.1f, 10.0f);
            viewMatrix = Matrix.identity();
            modelMatrix = Matrix.translate(yRot, 0.0f, xRot);
            //modelMatrix = Matrix.multiply(Matrix.rotateX(xRot), Matrix.rotateY(yRot));
            modelMatrix = Matrix.multiply(Matrix.translate(0.0f, 0.0f, -1.0f), modelMatrix);
            modelViewMatrix = Matrix.multiply(viewMatrix, modelMatrix);
            modelViewProjectionMatrix = Matrix.multiply(projectionMatrix, modelViewMatrix);

            normalMatrix = modelViewMatrix.normalMatrix();
            glUniformMatrix4fv(modelViewProjectionMatrixId, 1, true, modelViewProjectionMatrix.data(), 0);
            glUniformMatrix4fv(modelViewMatrixId, 1, true, modelViewMatrix.data(), 0);
            glUniformMatrix4fv(normalMatrixId, 1, true, normalMatrix.data(), 0);

            // Bind texture to texture unit 0
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, textureDataBufferId.get(0));

            // Set shader uniform variables
            glUniform4f(ambientId, 0.1f, 0.1f, 0.1f, 1.0f);
            glUniform4f(diffuseId, 1.0f, 1.0f, 1.0f, 1.0f);
            glUniform4f(specularId, 1.0f, 1.0f, 1.0f, 1.0f);
            glUniform1f(specularCoefficientId, 100.0f);
            glUniform3f(lightPositionId, 2.0f, 2.0f, 2.0f);
            glUniform4f(lightColorId, 1.0f, 1.0f, 1.0f, 0.0f);
            glUniform1f(attenuationFactorId, 0.5f);
            glUniform1i(textureSamplerId, 0);

            // Set shader attribute variables to vbo's
            glEnableVertexAttribArray(vertexId);
            glBindBuffer(GL_ARRAY_BUFFER, vertexBufferId.get(0));
            glVertexAttribPointer(vertexId, 3, GL_FLOAT, false, 0, 0);
            glEnableVertexAttribArray(normalId);
            glBindBuffer(GL_ARRAY_BUFFER, normalBufferId.get(0));
            glVertexAttribPointer(normalId, 3, GL_FLOAT, false, 0, 0);
            glEnableVertexAttribArray(texCoordId);
            glBindBuffer(GL_ARRAY_BUFFER, textureCoordBufferId.get(0));
            glVertexAttribPointer(texCoordId, 2, GL_FLOAT, false, 0, 0);

            // Draw
            glDrawArrays(GL_TRIANGLES, 0, numberOfVertices);

            // Disable attribute arrays
            glDisableVertexAttribArray(vertexId);
            glDisableVertexAttribArray(normalId);
            glDisableVertexAttribArray(texCoordId);

            // Poll for events
            glfwPollEvents();

            // Swap buffers
            glfwSwapBuffers(window);

        } // Check if the ESC key was pressed or the wiondow was closed
        while (!glfwGetKey(window, GLFW_KEY_ESC) &&
                 !glfwWindowShouldClose(window));

        // Deallocate buffer memory
        glDeleteBuffers(1, vertexBufferId, 0);
        glDeleteBuffers(1, normalBufferId, 0);
        glDeleteBuffers(1, textureCoordBufferId, 0);
        glDeleteTextures(1, textureDataBufferId, 0);
        glDeleteProgram(programId);

        // Close OpenGL window and terminate GLFW
        glfwDestroyWindow(window);
        glfwTerminate();
    }
}
