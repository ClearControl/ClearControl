__kernel void run(__read_only image2d_t input, __write_only image2d_t output,const int Nx, const int Ny)
{
const sampler_t smp = CLK_NORMALIZED_COORDS_FALSE | CLK_ADDRESS_NONE | CLK_FILTER_NEAREST; 

int2 coord = (get_global_id(1), get_global_id(0));

uint4 pixel = read_imageui(input, smp, coord);


write_imageui(output, coord, pixel);

}