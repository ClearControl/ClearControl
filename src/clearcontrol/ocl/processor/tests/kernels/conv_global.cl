__kernel void run(__read_only image2d_t input, __write_only image2d_t output,const int Nx, const int Ny)
{
	const sampler_t sampler = CLK_NORMALIZED_COORDS_FALSE | CLK_ADDRESS_NONE | CLK_FILTER_NEAREST; 

	int2 pos = (int2)(get_global_id(0),get_global_id(1));
	uint4 outPix = (uint4)(1,0,0,0);
	
	int N = 3;
	float res = 0.f;
	float weightSum = 0.f;
	 
	for(int i=-N;i<=N;i++){
		for(int j=-N;j<=N;j++){
			
				int2 pos1 = (int2)(pos.x+i,pos.y+j);
				uint4 pix1 = read_imageui(input,sampler,pos1);
				float weight = exp(-1.f*(i*i+j*j));
				res += weight*pix1.x;
				weightSum += weight;
		}
	}
	outPix.x = (uint)(res/weightSum);
	write_imageui(output,pos,outPix);
}