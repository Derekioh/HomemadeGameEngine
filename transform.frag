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
  float NdotL = max(dot(normal, light), 0.0);
  vec3 reflection = normalize(reflect(light, normal));
  float VdotR = max(dot(view, reflection), 0.0);
  float dist = length(light_position_3f - position);
  float attenuation = 1.0 / (1.0 + (attenuation_factor_1f * dist));

  vec4 texture_color = texture2D(textureSampler, texture);
  vec4 ambient_intensity = texture_color * ambient_color_4f;
  vec4 diffuse_intensity = NdotL * texture_color * diffuse_color_4f;
  vec4 specular_intensity = pow(VdotR, specular_coefficient_1f) * specular_color_4f;
  vec4 intensity = ambient_intensity + diffuse_intensity + specular_intensity;

  gl_FragColor = attenuation * light_color_4f * intensity;
}
