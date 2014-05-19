__kernel void run(__read_only image3d_t input, __global short* output,const int Nx, const int Ny, const int Nz)
{
  const sampler_t sampler = CLK_NORMALIZED_COORDS_FALSE |	CLK_ADDRESS_CLAMP_TO_EDGE |	CLK_FILTER_NEAREST ;

  int i = get_global_id(0);
  int j = get_global_id(1);
  

  float sum = 0;


   for(int k = 0; k < Nz;++k)
  		sum += read_imageui(input,sampler,(int4)(i,j,k,0)).x;;
  

  output[i+j*Nx] = (short)(sum/Nz);


  
}
