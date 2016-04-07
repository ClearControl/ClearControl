__kernel void run(__read_only image2d_t input, __write_only image2d_t output,const int Nx, const int Ny)
{
	const sampler_t sampler = CLK_NORMALIZED_COORDS_FALSE | CLK_ADDRESS_NONE | CLK_FILTER_NEAREST; 

	int2 pos0 = (get_global_id(1), get_global_id(0));

	uint4 pix0 = read_imageui(input, sampler, pos0);
   
  const int BLOCKSIZE = 3;
  const float sigma = 30;

  float res = 0.f;
  float sum = 0.f;

	uint4 pix1;
	int2 pos1;
	
  for(int k = -BLOCKSIZE;k<=BLOCKSIZE;k++){
  	for(int m = -BLOCKSIZE;m<=BLOCKSIZE;m++){
  		  pos1 = (int2)(pos0.x+k,pos0.y+m);
				uint4 pix1 = read_imageui(input,sampler,pos1);
				uint dpix = pix1.x-pix0.x;
    		
    	  float weight = exp(-1.f/sigma/sigma*dpix*dpix);
    	  res += pix1.x*weight;
    		sum += weight;
    	
    	}
    }

  pix1.x = (uint)(res/sum);
  pix1.x = 18; 
	write_imageui(output,pos0,pix1);
}
