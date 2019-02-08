import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.nio.ByteBuffer;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static com.badlogic.jglfw.Glfw.*;
import static com.badlogic.jglfw.gl.GL.*;
import com.badlogic.jglfw.utils.Memory;

public class Model{

    private final int BYTES_PER_FLOAT = 4;
    private final int BYTES_PER_INT = 4;
    private final int SIZE_OF_VECTOR = 3;
    private final int SIZE_OF_TEXTURE_COORD = 2;
    private final int MATERIAL_ARRAY_SIZE = 10;
    private final int MAT_AMB_INDEX = 1;
    private final int MAT_DIF_INDEX = 4;
    private final int MAT_SPE_INDEX = 7;
    private final int MAT_SIZE = 10;
    private final int RGB_SIZE = 3;

    public static int modelViewProjectionMatrixId; // mvp matrix handle
    public static int modelViewMatrixId;           // mv matrix handle
    public static int normalMatrixId;              // normal matrix handle

    public static int ambientId;                 // shader material ambient handle
    public static int diffuseId;                 // shader material diffuse handle
    public static int specularId;                // shader material specular handle
    public static int specularCoefficientId;     // shader material specular handle
    public static int lightPositionId;           // shader light position handle
    public static int lightColorId;              // shader light color handle
    public static int attenuationFactorId;       // shader light attenuation handle
    public static int textureSamplerId;
    //public static int bumpSamplerId;

    public static float[] boundingVertices = new float[24]; //static????

    private ArrayList<Mesh> meshArray;
    private int number_of_vertices;
    private int number_of_normals;
    private boolean isLoaded;

    private FloatBuffer vertexBuffer; // vertex coordinates buffer
    private FloatBuffer normalBuffer; // vertex normals buffer
    private ByteBuffer textureDataBuffer;
    private FloatBuffer textureCoordBuffer; // vertex texture coordinates buffer

    private ArrayList<Integer> vertexIdArray = new ArrayList<Integer>();
    private ArrayList<Integer> normalIdArray = new ArrayList<Integer>();
    private ArrayList<Integer> textureIdArray = new ArrayList<Integer>();
    
    private IntBuffer vertexBufferId; // vertex buffer handle
    private IntBuffer normalBufferId; // normal buffer handle
    private IntBuffer textureCoordBufferId; // texture coordinate buffer handle
    private IntBuffer textureDataBufferId; // texture coordinate buffer handle
    //private IntBuffer bumpDataBufferId;
      
    Light light;

    //PRE: strng that holds the file name we are gonna parse
    //POST: setup the vbo
    public void load(String fileName){
	try{
	    meshArray = read_file(fileName);
	    int counter = 0;

	    //for (int i = 0; i < 24; i++){
	    //	System.out.println(boundingVertices[i]);
	    //}

	    for (Mesh mesh : meshArray){
	    
		float[] vertices = mesh.vertices;
		float[] normals  = mesh.normals;
		float[] textureCoordinates = mesh.textureCoordinate;

		number_of_vertices = vertices.length;
		int buffer_size_vert = number_of_vertices * BYTES_PER_FLOAT;
		vertexBuffer = Memory.malloc(buffer_size_vert).asFloatBuffer();
		vertexBufferId = Memory.malloc(BYTES_PER_INT).asIntBuffer();

		number_of_normals = normals.length;
		int buffer_size_norm = number_of_normals * BYTES_PER_FLOAT;
		normalBuffer = Memory.malloc(buffer_size_norm).asFloatBuffer();
		normalBufferId = Memory.malloc(BYTES_PER_INT).asIntBuffer();

		// Create vertex buffer
		vertexBuffer.put(vertices);
		glGenBuffers(1, vertexBufferId, 0);
		glBindBuffer(GL_ARRAY_BUFFER, vertexBufferId.get(0));
		glBufferData(GL_ARRAY_BUFFER, buffer_size_vert, vertexBuffer, 0, GL_STATIC_DRAW);
	    
		// //create normal buffer
		normalBuffer.put(normals);
		glGenBuffers(1, normalBufferId, 0);
		glBindBuffer(GL_ARRAY_BUFFER, normalBufferId.get(0));
		glBufferData(GL_ARRAY_BUFFER, buffer_size_norm, normalBuffer, 0, GL_STATIC_DRAW);

		// Create texture coordinate buffer
		int buffer_size_text = textureCoordinates.length * BYTES_PER_FLOAT;
		textureCoordBuffer = Memory.malloc(buffer_size_text).asFloatBuffer();
		textureCoordBufferId = Memory.malloc(BYTES_PER_INT).asIntBuffer();
		textureCoordBuffer.put(textureCoordinates);
		glGenBuffers(1, textureCoordBufferId, 0);
		glBindBuffer(GL_ARRAY_BUFFER, textureCoordBufferId.get(0));
		glBufferData(GL_ARRAY_BUFFER, buffer_size_text, textureCoordBuffer, 0, GL_STATIC_DRAW);

		vertexIdArray.add(vertexBufferId.get(0));
		normalIdArray.add(normalBufferId.get(0));
		textureIdArray.add(textureCoordBufferId.get(0));

	    }
	    
	    light = new Light();
	    light.set_position(2.0f, 2.0f, 2.0f);
	    light.set_attenuation(0.5f);

	    isLoaded = true;
	}catch (Exception e){
	    isLoaded = false;
	    System.out.println("Loaded is false");
	    e.printStackTrace();
	}
	
    }

    //PRE; void
    //POST: draws the image on the screen using vbo
    public void draw(){

	if (isLoaded){
	    
	    light.activate();

	    //System.out.println("draw");
	    for (int index = 0; index < vertexIdArray.size(); index++){
		
		//CONSTANT, ambient, diffuse, specular
		Material materials = meshArray.get(index).material;
		float ax = materials.ambient[0];
		float ay = materials.ambient[1];
		float az = materials.ambient[2];

		float dx = materials.diffuse[0];
		float dy = materials.diffuse[1];
		float dz = materials.diffuse[2];

		float sx = materials.specular[0];
		float sy = materials.specular[1];
		float sz = materials.specular[2];

		float sc = materials.specular_coefficient;

		// Bind texture to texture unit 0
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, materials.textureId.get(0));

		// Set shader uniform variables
		glUniform4f(ambientId, ax, ay, az, 1.0f);
		glUniform4f(diffuseId, dx, dy, dz, 1.0f);
		glUniform4f(specularId, sx, sy, sz, 1.0f);
		glUniform1f(specularCoefficientId, sc);
		glUniform3f(lightPositionId, light.lightX, light.lightY,
			    light.lightZ);
		glUniform4f(lightColorId, light.lightR, light.lightG,
			    light.lightB, light.lightA);
		glUniform1f(attenuationFactorId, light.attenuationFactor);
		glUniform1i(textureSamplerId, 0);

		// Set shader attribute variables to vbo's
		glEnableVertexAttribArray(Window.vertexId);
		glBindBuffer(GL_ARRAY_BUFFER, vertexIdArray.get(index));
		glVertexAttribPointer(Window.vertexId, 3, GL_FLOAT, false, 0,0);

		glEnableVertexAttribArray(Window.normalId);
		glBindBuffer(GL_ARRAY_BUFFER, normalIdArray.get(index));
		glVertexAttribPointer(Window.normalId, 3, GL_FLOAT, false, 0,0);

		//fix with material maybe?
		glEnableVertexAttribArray(Window.texCoordId);
		glBindBuffer(GL_ARRAY_BUFFER, textureIdArray.get(index));
		glVertexAttribPointer(Window.texCoordId, 2, GL_FLOAT, false, 0, 0);

		glDrawArrays(GL_TRIANGLES, 0,
			     number_of_vertices / SIZE_OF_VECTOR);

	    }

	}
    }

    //PRE: void
    //POST: cleanup buffers
    public void cleanup(){
	//dealloacate
	for (int index = 0; index < vertexIdArray.size(); index++){
	    glDeleteBuffers(1, vertexIdArray.get(index));
	    glDeleteBuffers(1, normalIdArray.get(index));
	    meshArray = new ArrayList<Mesh>();
	}
    }

    //PRE: string of that holds the line we are parsing that includes normals and textureCoords
    //POST: returns the indices of the face (vertex, normal, textureCoord)
    public int[] read_face_normals_textures(String line){
	Scanner scanner = new Scanner(line);
	int[] indices = new int[SIZE_OF_VECTOR * 3];
	String word;
	int deliminator, deliminator2;

	scanner.next(); //removes f
	for(int index = 0; index < SIZE_OF_VECTOR * 3; index+=3){
	    word = scanner.next();
	    deliminator = word.indexOf("/");
	    deliminator2 = word.indexOf("/", deliminator + 1);
	    
	    indices[index] = Integer.parseInt(word.substring(0, deliminator)); //vertex
	    indices[index + 2] = Integer.parseInt(word.substring(deliminator2 + 1)); //normal
	    indices[index + 1] = Integer.parseInt(word.substring(deliminator + 1, deliminator2)); //texturecoord

	}
	return indices;
	
    }

    //PRE: string of that holds the line we are parsing that includes normals
    //POST: returns the integer face array of length 3
    public int[] read_face_normals(String line){
	Scanner scanner = new Scanner(line);
	int[] indices = new int[SIZE_OF_VECTOR * 2];
	String word;
	int deliminator;

	scanner.next(); //removes f
	for(int index = 0; index < SIZE_OF_VECTOR * 2; index+=2){
	    word = scanner.next();
	    deliminator = word.indexOf("/");
	    
	    indices[index] = Integer.parseInt(word.substring(0, deliminator));
	    indices[index + 1] = Integer.parseInt(word.substring(deliminator + 2));
	}
	return indices;
	
    }

    //PRE: string of that holds the line we are parsing
    //POST: returns the integer face array of length 3
    public int[] read_face(String line){
	Scanner scanner = new Scanner(line);
	int[] vector = new int[SIZE_OF_VECTOR];
	//string[] word;
	scanner.next(); //removes the v or f
	for(int index = 0; index < SIZE_OF_VECTOR; index++){
	    vector[index] = Integer.parseInt(scanner.next());
	}
	return vector;
	
    }

    //PRE: string of line
    //POST: returns the vector of vertices
    public  float[] read_vertex(String line){
	Scanner scanner = new Scanner(line);
	float[] vector = new float[SIZE_OF_VECTOR];
	scanner.next(); //removes the v or f
	for(int index = 0; index < SIZE_OF_VECTOR; index++){
	    vector[index] = Float.parseFloat(scanner.next());
	}
	return vector;
	
    }

    //PRE: string of line
    //POST: returns the array of texture coords
    public  float[] read_textureCoord(String line){
	Scanner scanner = new Scanner(line);
	float[] textureCoord = new float[SIZE_OF_TEXTURE_COORD];
	scanner.next(); //removes the vt
	for(int index = 0; index < SIZE_OF_TEXTURE_COORD; index++){
	    textureCoord[index] = Float.parseFloat(scanner.next());
	}
	return textureCoord;
	
    }

    
    //Pre: string of line we are parsing
    //POST: return single float
    public  float read_mat_constant(String line){
	Scanner scanner = new Scanner(line);
	float constant;
	scanner.next();
	constant = Float.parseFloat(scanner.next());
	return constant;
    }

    //PRE: string of line we are parsing
    //POST: return vector float
    public  float[] read_mat(String line){
	Scanner scanner = new Scanner(line);
	float[] vertex = new float[SIZE_OF_VECTOR];
	scanner.next();
	for (int index = 0; index < SIZE_OF_VECTOR; index++){
	    vertex[index] = Float.parseFloat(scanner.next());
	}
	return vertex;
    }

    //PRE: string of the line being parsed
    //POST: return the name of the material name
    public String read_mat_name(String line){
	Scanner scanner = new Scanner(line);
	String name;
	scanner.next();
	name = scanner.next();
	return name;
    }

    //PRE: string of the line being parsed
    //POST: return both the command of the line and texture name
    public String read_mat_texture(String line){
	Scanner scanner = new Scanner(line);
	String returnString;
	scanner.next();
	returnString = scanner.next();
	return returnString;
    }

    //PRE: take file name as string
    //POST: parse through the file, read the bytes in it, flip the bytes for order preferance in OpenGL,
    //      and load the texture in OpenGL to return just the ID.
    public IntBuffer parse_texture(String fileName) throws IOException{
	FileInputStream in = new FileInputStream(fileName);
	BufferedReader br = new BufferedReader(new InputStreamReader(in));
	String line = br.readLine();
	int bite;
	int imgWidth = 0;
	int imgHeight = 0;
	byte[] textureDataArray;
	int headerLength = 0;
	boolean isComment, isType, isBitDepth;
	//read the header
	while (line != null){
	    headerLength += line.length() + 1;
	    isComment = line.startsWith("#");
	    isType    = line.startsWith("P");
	    isBitDepth = line.startsWith("255"); // since we are assumming image size is a factor of 2
	    if (isComment == false && isType == false){
		if (isBitDepth){
		    break;
		}else{
		    String[] parts = line.split(" ");
		    imgWidth = Integer.parseInt(parts[0]);
		    imgHeight = Integer.parseInt(parts[1]);
		}
	    }
	    line = br.readLine();
	}
	br.close();

	//read the file
	File file = new File(fileName);
	FileInputStream fis = new FileInputStream(file);
	textureDataArray = new byte[imgWidth*imgHeight*3];
	fis.skip(headerLength);
	fis.read(textureDataArray);
	fis.close();

	//flip bytes
	byte[] flippedTextureDataArray = new byte[imgWidth*imgHeight*3];
	int flippedTextureIndex = 0;
	int textureIndex = 0;
	for(int row = imgHeight - 1; row >= 0; row--){
	    for(int col = 0; col < imgWidth ; col++){
		for(int channel = 0; channel < 3; channel++){
		    textureIndex = (row * imgWidth * 3) + (col * 3) + channel;
		    flippedTextureDataArray[flippedTextureIndex] = textureDataArray[textureIndex];
		    flippedTextureIndex++;
		}
	    }
	}


	// Load texture
	textureDataBuffer = Memory.malloc(flippedTextureDataArray.length);
	textureDataBufferId = Memory.malloc(BYTES_PER_INT).asIntBuffer();
	textureDataBuffer.put(flippedTextureDataArray);
	glGenTextures(1, textureDataBufferId, 0);
	glBindTexture(GL_TEXTURE_2D, textureDataBufferId.get(0));
	glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, imgWidth, imgHeight, 0, GL_RGB, GL_UNSIGNED_BYTE, textureDataBuffer, 0);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

	return textureDataBufferId;

    }

    //PRE: a min and max x,y,z values
    //POST: returns a list of vertices for the 8 points of the bounding box for collision
    public float[] calculateBoundingBox(float minX, float minY, float minZ,
					float maxX, float maxY,float maxZ){
	float[] returnFloat = new float[24];
	//bottom corner 1
	returnFloat[0] = minX;
	returnFloat[1] = minY;
	returnFloat[2] = minZ;
	//bottom corner 2
	returnFloat[3] = maxX;
	returnFloat[4] = minY;
	returnFloat[5] = minZ;
	//bottom corner 3
	returnFloat[6] = maxX;
	returnFloat[7] = minY;
	returnFloat[8] = maxZ;
	//bottom corner 4
	returnFloat[9] = minX;
	returnFloat[10] = minY;
	returnFloat[11] = maxZ;
	//top corner 1
	returnFloat[12] = minX;
	returnFloat[13] = maxY;
	returnFloat[14] = minZ;
	//top corner 2
	returnFloat[15] = maxX;
	returnFloat[16] = maxY;
	returnFloat[17] = minZ;
	//top corner 3
	returnFloat[18] = maxX;
	returnFloat[19] = maxY;
	returnFloat[20] = maxZ;
	//top corner 4
	returnFloat[21] = minX;
	returnFloat[22] = maxY;
	returnFloat[23] = maxZ;

	return returnFloat;
    }

    ///PRE: takes file name
    //POST: outputs a float array containing constant, ambient, diffuse, and specular
    //      in that order
    public  ArrayList<Material> load_mat(String fileName, ArrayList<Material> materialArray) throws IOException{

 	BufferedReader matReader = null;
	matReader = new BufferedReader(new FileReader(fileName)); 
	Material material = new Material();
	boolean seenNewMtl = false;
	String  line = matReader.readLine();
	String textureName;
	while (line != null){
	    String word = "";
	    if (line.length() > 1){
		word = line.substring(0,2);
	    }
	    if (word.equals("Ns")){
		material.specular_coefficient = read_mat_constant(line);
	    }else if (word.equals("Ka")){
		material.ambient = read_mat(line);
	    }else if (word.equals("Kd")){
		material.diffuse = read_mat(line);
	    }else if (word.equals("Ks")){
		material.specular = read_mat(line);	     
	    }else if (word.equals("ne")){
		if (!seenNewMtl){
		    seenNewMtl = true;
		}else{
		    materialArray.add(material);
		    material = new Material();
		}
		material.name = read_mat_name(line);
	    }else if(word.equals("ma")){
		textureName = read_mat_texture(line);
		material.textureId = parse_texture(textureName);
	    }else{
		System.out.println("line: "+line);
	    }
	    line = matReader.readLine();
	}
	if (matReader != null){
	    matReader.close();
	}
	 
	materialArray.add(material);    

	return materialArray;
    }

    //PRE: string of file name
    //POST: returns the array that we can use for the VBO, contains vertices, normals, materials
    public ArrayList<Mesh> read_file(String fileName) throws IOException{
	ArrayList<float[]> vertices = new ArrayList<float[]>();
	ArrayList<float[]> normals = new ArrayList<float[]>();
	ArrayList<float[]> textureCoords = new ArrayList<float[]>();

	ArrayList<Float> orderedVertices = new ArrayList<Float>();
	ArrayList<Float> orderedNormals = new ArrayList<Float>();
	ArrayList<Float> orderedTextureCoords = new ArrayList<Float>();

	float[] flatVertexArray;
	float[] flatNormalArray;
	float[] flatMaterialArray = new float[MAT_SIZE];
	float[] flatTextureCoordArray;

	ArrayList<Material> materialArray = new ArrayList<Material>();
	ArrayList<Mesh> meshArray = new ArrayList<Mesh>();
	ArrayList<float[]> vertNormMatArray = new ArrayList<float[]>();

	float maxX = 0.0f;
	float maxY = 0.0f;
	float maxZ = 0.0f;
	float minX = 0.0f;
	float minY = 0.0f;
	float minZ = 0.0f;
	int count = 0;

	boolean seenUseMtl = false;
	Material currentMaterial = new Material();
	BufferedReader reader = new BufferedReader(new FileReader(fileName));
	String line = reader.readLine();
	while (line != null){
	    String word = "";
	    if (line.length() > 1){
		word = line.substring(0, 2);
	    }
	    if (word.equals("v ")){
		vertices.add(read_vertex(line));
	    }else if (word.equals("f ")){
		//check if normals are not included
		if (line.indexOf("/") == -1){
		    int[] face = read_face(line);
		    for (int faceIndex = 0; faceIndex < SIZE_OF_VECTOR; 
			 faceIndex++){
			float[] vertex = vertices.get(face[faceIndex] - 1);
			for (int vertexIndex = 0; vertexIndex < SIZE_OF_VECTOR; 
			     vertexIndex++){
			    orderedVertices.add(vertex[vertexIndex]);
			}
		    }
		}else{ 
		    //check if texture is included as well
		    if (line.indexOf("//") == -1){
			int[] face = read_face_normals_textures(line);
			for (int faceIndex = 0; faceIndex < face.length; faceIndex += 3){
			    float[] vertex = vertices.get(face[faceIndex] - 1);
			    float[] normal = normals.get(face[faceIndex + 2] - 1);
			    float[] texture = textureCoords.get(face[faceIndex + 1] - 1);
			    for (int vertexIndex = 0; vertexIndex < vertex.length; vertexIndex++){
				orderedVertices.add(vertex[vertexIndex]);
				orderedNormals.add(normal[vertexIndex]);
			    }
			    orderedTextureCoords.add(texture[0]);
			    orderedTextureCoords.add(texture[1]);
			}
		    }else{
			// just read normals and vertices
			int[] face = read_face_normals(line);
			for (int faceIndex = 0; faceIndex < face.length; faceIndex += 2){
			    float[] vertex = vertices.get(face[faceIndex] - 1);
			    float[] normal = normals.get(face[faceIndex + 1] - 1);
			    for (int vertexIndex = 0; vertexIndex < SIZE_OF_VECTOR; vertexIndex++){
				orderedVertices.add(vertex[vertexIndex]);
				orderedNormals.add(normal[vertexIndex]);
			    }
			}
		    }
		}

	    }else if (word.equals("vn")){
		normals.add(read_vertex(line));

	    }else if (word.equals("vt")){
		textureCoords.add(read_textureCoord(line));
	    }else if (word.equals("mt")){
		Scanner scanner = new Scanner(line);
		scanner.next(); //skip mtllib
		String matFileName = scanner.next();
		materialArray = load_mat(matFileName, materialArray);
		scanner.close();
	    }else if (word.equals ("us")){
		if (!seenUseMtl){
		    seenUseMtl = true;
		}else{
		    //put the vertices in order
		    flatVertexArray = new float[orderedVertices.size()];
		    for (int index = 0; index < orderedVertices.size(); index++){
			flatVertexArray[index] = orderedVertices.get(index);

			//this handles finding the min and max x,y,z values for collision
			if (count < 3){
			    count++;
			}else{
			    count = 0;
			}

			if (count == 0){
			    if (orderedVertices.get(index) > maxX){
				maxX = orderedVertices.get(index);
			    }if (orderedVertices.get(index) < minX){
				minX = orderedVertices.get(index);
			    }
			}
			if (count == 1){
			    if (orderedVertices.get(index) > maxY){
				maxY = orderedVertices.get(index);
			    }if (orderedVertices.get(index) < minY){
				minY = orderedVertices.get(index);
			    }
			}
			if (count == 2){
			    if (orderedVertices.get(index) > maxZ){
				maxZ = orderedVertices.get(index);
			    }if (orderedVertices.get(index) < minZ){
				minZ = orderedVertices.get(index);
			    }
			}
		    }

		    //put normals in order
		    flatNormalArray = new float[orderedNormals.size()];
		    for (int index = 0; index < orderedNormals.size(); index++){
			flatNormalArray[index] = orderedNormals.get(index);
		    }

		    //put texture Coords in order
		    flatTextureCoordArray = new float[orderedTextureCoords.size()];
		    for (int index = 0; index < orderedTextureCoords.size(); index++){
			flatTextureCoordArray[index] = orderedTextureCoords.get(index);
		    }

		    Mesh mesh = new Mesh();
		    mesh.vertices = flatVertexArray;
		    mesh.normals = flatNormalArray;
		    mesh.material = currentMaterial;
		    mesh.textureCoordinate = flatTextureCoordArray;
		    meshArray.add(mesh);

		}
		Scanner scanner = new Scanner(line);
		scanner.next();
		String matName = scanner.next();
		for(Material mat: materialArray){
		    if (mat.name.equals(matName)){
			currentMaterial = mat;
		    }
		}	    
	    }
	    line = reader.readLine();
	}
	//System.out.println("end file");
	reader.close();

	//put the vertices in order
	flatVertexArray = new float[orderedVertices.size()];
	for (int index = 0; index < orderedVertices.size(); index++){
	    flatVertexArray[index] = orderedVertices.get(index);

	    //this handles finding the min and max x,y,z values for collision
	    if (count < 3){
		count++;
	    }else{
		count = 0;
	    }

	    if (count == 0){
		if (orderedVertices.get(index) > maxX){
		    maxX = orderedVertices.get(index);
		}if (orderedVertices.get(index) < minX){
		    minX = orderedVertices.get(index);
		}
	    }
	    if (count == 1){
		if (orderedVertices.get(index) > maxY){
		    maxY = orderedVertices.get(index);
		}if (orderedVertices.get(index) < minY){
		    minY = orderedVertices.get(index);
		}
	    }
	    if (count == 2){
		if (orderedVertices.get(index) > maxZ){
		    maxZ = orderedVertices.get(index);
		}if (orderedVertices.get(index) < minZ){
		    minZ = orderedVertices.get(index);
		}
	    }
	}

	//put normals in order
	flatNormalArray = new float[orderedNormals.size()];
	for (int index = 0; index < orderedNormals.size(); index++){
	    flatNormalArray[index] = orderedNormals.get(index);
	}

	//put texture Coords in order
	flatTextureCoordArray = new float[orderedTextureCoords.size()];
	for (int index = 0; index < orderedTextureCoords.size(); index++){
	    flatTextureCoordArray[index] = orderedTextureCoords.get(index);
	}

	//System.out.println(minX + " " + minY + " " + minZ + " " + maxX + " " + maxY + " " + maxZ);

	 //put min and max x,y,z values in order
	boundingVertices = calculateBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);

	Mesh mesh = new Mesh();
	mesh.vertices = flatVertexArray;
	mesh.normals = flatNormalArray;
	mesh.material = currentMaterial;
	mesh.textureCoordinate = flatTextureCoordArray;
	meshArray.add(mesh);

	return meshArray;
	
    }
    
    private class Material{
	public float[] ambient = new float[SIZE_OF_VECTOR];
	public float[] specular = new float[SIZE_OF_VECTOR];
	public float[] diffuse = new float[SIZE_OF_VECTOR];
	public float specular_coefficient = 0.0f;
	public String name = "";
	public IntBuffer textureId;
	
    }
    
    private class Mesh{
	public float[] vertices;
	public float[] normals;
	public Material material;
	public float[] textureCoordinate;
    }
}

