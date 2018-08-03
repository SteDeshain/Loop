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
		
	vec4 sumColor = vec4(0.0);
	float sumWeight = 0.0;
	for(int y = -blurRadius; y <= blurRadius; y++)
	{
		vec2 curCoord = vec2(texCoord.x, texCoord.y + float(y) * pixelSize.y);
		float length = float(y);
		float r = length / float(blurRadius) * 2.0;	//pick 0 ~ 2 as the whole range
		float curWeight = gaussian(r);
		sumWeight += curWeight;
		vec4 curColor = v_color * texture2D(u_texture, curCoord) * curWeight;
		sumColor += curColor;
	}
	
	//gl_FragColor = v_color * texture2D(u_texture, texCoord);
	gl_FragColor = sumColor / sumWeight;
}