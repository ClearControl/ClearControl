#define FSIZE 5
#define SIGMA 10.f

__kernel void run(__read_only image2d_t input, __global short* output,const int Nx, const int Ny)
{
  const sampler_t sampler = CLK_NORMALIZED_COORDS_FALSE |	CLK_ADDRESS_CLAMP_TO_EDGE |	CLK_FILTER_NEAREST ;

  uint i = get_global_id(0);
  uint j = get_global_id(1);

  float res = 0;
  float sum = 0;
  
  for(int k = -FSIZE;k<=FSIZE;k++){
    for(int m = -FSIZE;m<=FSIZE;m++){

    uint4 pix1 = read_imageui(input,sampler,(int2)(i+k,j+m));
    float weight = exp(-1.f/SIGMA/SIGMA*(k*k+m*m));
    res += pix1.x*weight;
    sum += weight;

    }
  }

  output[i+Nx*j] = (short)(res/sum);
  
 
  
}
