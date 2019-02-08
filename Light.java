
class Light{

    public float lightX, lightY, lightZ;
    public float lightR, lightG, lightB, lightA;
    public float attenuationFactor;
    public boolean active = false;

    public Light(){
	lightX = 0.0f;
	lightY = 0.0f;
	lightZ = 0.0f;
	lightR = 1.0f;
	lightG = 1.0f;
	lightB = 1.0f;
	lightA = 0.0f;
	attenuationFactor = 0.2f;
    }

    public void set_position(float x, float y, float z){
	lightX = x;
	lightY = y;
	lightZ = z;
    }

    public void set_color(float r, float g, float b){
	lightR = r;
	lightG = g;
	lightB = b;
    }

    public void set_attenuation(float factor){
	attenuationFactor = factor;
    }

    public void activate(){
	if (active){
	    active = false;
	}else{
	    active = true;
	}
    }

}