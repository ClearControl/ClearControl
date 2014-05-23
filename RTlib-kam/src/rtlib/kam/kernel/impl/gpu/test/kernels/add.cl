
kernel void add_buffer(		const long3 offset,
							const long3 range,
							global const type* input,
							global type* output, 
							const type pValue) 
{
    int x = get_global_id(0);
  	int y = get_global_id(1);
  	int width = get_global_size(0);
  	
  	int stride = (int)((range.x*range.y)/(get_global_size(0)*get_global_size(1)));
  	
  	int indexblock = stride*(x+width*y);
  	
  	for(int s=0; s<stride; s++)
	{
		long index = s+indexblock; 
		output[index] = input[index]+pValue;
	}
}




#pragma OPENCL EXTENSION cl_khr_3d_image_writes : enable
kernel void add_image(	const long3 offset,
						const long3 range,
						read_only  image3d_t input, 
						write_only image3d_t output, 
						const type pValue) 
{
	const sampler_t sampler = CLK_NORMALIZED_COORDS_FALSE | CLK_ADDRESS_CLAMP_TO_EDGE | CLK_FILTER_NEAREST; 
    
    const int stridex = (int)(((int)(range.x))/(int)get_global_size(0));
    const int stridey = (int)(((int)(range.y))/(int)get_global_size(1));
    const int stridez = (int)(((int)(range.z))/(int)get_global_size(2));
    
    const int tx = get_global_id(0);
  	const int ty = get_global_id(1);
  	const int tz = get_global_id(2);
  	
    int x = stridex*tx;
    int y = stridey*ty;
    int z = stridez*tz;
  	
  	for(int sz=0; sz<stridez; sz++)
  	for(int sy=0; sy<stridey; sy++) 	
  	for(int sx=0; sx<stridex; sx++)
	{
		int4 pos0 = (int4)(x+sx,y+sy,z+sz,0);
		typefam4 pix0 = read_imageT(input, sampler, pos0);
		typefam4 pix1 = (typefam4)(pix0.x+ (typefam)pValue,0,0,0); //
		write_imageT(output,pos0,pix1);
		//printf("%d\n",pix1.x);
	}
	
	
	//write_imageT(output,(int4)(x,y,z,0),(typefam4)(range.y,0,0,0));
}
