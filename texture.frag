#version 120

uniform vec4 ambient_color_4f; // material ambient reflectance color
uniform vec4 diffuse_color_4f; // meterial diffuse reflectance color
uniform vec4 specular_color_4f;// material specular reflectance color
uniform float specular_coefficient_1f; // specular coefficient
uniform vec3 light_position_3f; // location of light
uniform vec4 light_color_4f;    // color of light
uniform float attenuation_factor_1f; // distance attenuation scale factor
uniform sampler2D textureSampler;

varying vec3 position; // vertex position, camera space
varying vec3 normal;   // vertex normal, camera space
varying vec3 view;     // view vector, camera space
varying vec3 light;    // light vector, camera space
varying vec2 texture;  // vertex texture coordinates

void main() {
  float NdotL, VdotR;      // diffuse and specular factors
  vec3 reflection;         // reflection vector
  vec4 texture_color;      // texture color
  vec4 ambient_intensity;  // ambient reflection intensity
  vec4 diffuse_intensity;  // diffuse reflection intensity
  vec4 specular_intensity; // specular reflection intensity
  vec4 intensity;
  float dist;
  float attenuation;

  NdotL = max(dot(normal, light), 0.0);
  reflection = normalize(reflect(light, normal));
  VdotR = max(dot(view, reflection), 0.0);
  dist = length(light_position_3f - position);
  attenuation = 1.0 / (1.0 + (attenuation_factor_1f * dist));

  texture_color = texture2D(textureSampler, texture);

  ambient_intensity = ambient_color_4f * texture_color;
  diffuse_intensity = NdotL * diffuse_color_4f * texture_color;
  specular_intensity = pow(VdotR, specular_coefficient_1f) * specular_color_4f;
  intensity = ambient_intensity + diffuse_intensity + specular_intensity;

  gl_FragColor = attenuation * light_color_4f * intensity;
}
