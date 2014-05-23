#define SIGMA 15.f

__kernel void run(__read_only image3d_t input, __global short* output,const int Nx, const int Ny, const int Nz, const int FSIZE)
{
  const sampler_t sampler = CLK_NORMALIZED_COORDS_FALSE |	CLK_ADDRESS_CLAMP_TO_EDGE |	CLK_FILTER_NEAREST ;

  uint i = get_global_id(0);
  uint j = get_global_id(1);
  uint k = get_global_id(2);

  int val0 = read_imageui(input,sampler,(int4)(i,j,k,0)).x;
  
  float res = 0;
  float sum = 0;
  

  for(int i2 = -FSIZE;i2<=FSIZE;i2++){
    for(int j2 = -FSIZE;j2<=FSIZE;j2++){
  	  for(int k2 = -FSIZE;k2<=FSIZE;k2++){

  		int val1 = read_imageui(input,sampler,(int4)(i+i2,j+j2,k+k2,0)).x;



		int dist = (val0-val1)*(val0-val1);
		
			
		float weight = exp(-1.f/SIGMA/SIGMA*dist);
		res += val1*weight;
  		sum += weight;
  		

  	  }
  	}
  }

	if (i+j*Nx+k*Nx*Ny<Nx*Ny*Nz)	
  	output[i+j*Nx+k*Nx*Ny] = (short)(res/sum);

  
}

