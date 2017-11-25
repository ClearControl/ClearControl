#version 150
 
uniform sampler2D texUnit; 
 
uniform float minimum;
uniform float maximum;
uniform float gamma;
 
in vec2 ftexcoord;

out vec4 outColor;
 
void main()
{
	float value = texture(texUnit, ftexcoord).x;
	float mappedvalue = (value-minimum)/(maximum-minimum);
	float gammavalue = pow(mappedvalue,gamma);
	outColor = vec4(gammavalue,gammavalue,gammavalue,1);
}
