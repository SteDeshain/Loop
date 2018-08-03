//#ifdef GL_SE
precision mediump float;
//#endif
varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform vec2 u_windowSize;
uniform int u_blurRadius;

float gaussian(float r)
{
	//a = 0.39894228040143267793994605993438
	//99% falls in 0 ~ 3. I pick 0 ~ 2 as the whole range
	//f(0) = 0.4(as well as a)
	return exp(r * r / -2.0) * 0.398942;
}

void main()
{
	vec2 texCoord = v_texCoords;
	vec2 pixelSize = vec2(1.0 / u_windowSize.x, 1.0 / u_windowSize.y);
	
	int blurRadius = u_blurRadius;
	if(blurRadius < 0)
		blurRadius = -blurRadius;
		
	vec4 sumColorH = vec4(0.0);
	float sumWeightH = 0.0;
	for(int x = -blurRadius; x <= blurRadius; x++)
	{
		vec2 curCoord = vec2(texCoord.x + float(x) * pixelSize.x, texCoord.y);
		float length = float(x);
		float r = length / float(blurRadius) * 2.0;	//pick 0 ~ 2 as the whole range
		float curWeight = gaussian(r);
		sumWeightH += curWeight;
		vec4 curColor = v_color * texture2D(u_texture, curCoord) * curWeight;
		sumColorH += curColor;
	}
	vec4 tmpColor = sumColorH / sumWeightH;
	
	gl_FragColor = tmpColor;
}