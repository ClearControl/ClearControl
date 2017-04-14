

                                
__kernel void fuseavg2(    __read_only    image3d_t  imagea,
                           __read_only    image3d_t  imageb,
                           __write_only   image3d_t  imagedest
                     )
{
  const sampler_t intsampler  = CLK_NORMALIZED_COORDS_FALSE | CLK_ADDRESS_NONE | CLK_FILTER_NEAREST;
 
  const int x = get_global_id(0); 
  const int y = get_global_id(1);
  const int z = get_global_id(2);
  
  const int4 pos = (int4){x,y,z,0};

  const int valuea = read_imageui(imagea, intsampler, pos).x;
  const int valueb = read_imageui(imageb, intsampler, pos).x;
  
  const int value = (valuea+valueb)/2;
  
  write_imageui (imagedest, pos, (uint4){(uint)value,0,0,0});
}

__kernel void fuseavg4(    __read_only    image3d_t  imagea,
                           __read_only    image3d_t  imageb,
                           __read_only    image3d_t  imagec,
                           __read_only    image3d_t  imaged,
                           __write_only   image3d_t  imagedest
                     )
{
  const sampler_t intsampler  = CLK_NORMALIZED_COORDS_FALSE | CLK_ADDRESS_NONE | CLK_FILTER_NEAREST;
 
  const int x = get_global_id(0); 
  const int y = get_global_id(1);
  const int z = get_global_id(2);
  
  const int4 pos = (int4){x,y,z,0};

  const int valuea = read_imageui(imagea, intsampler, pos).x;
  const int valueb = read_imageui(imageb, intsampler, pos).x;
  const int valuec = read_imageui(imagec, intsampler, pos).x;
  const int valued = read_imageui(imaged, intsampler, pos).x;
  
  const int value = (valuea+valueb+valuec+valued)/4;
  
  write_imageui (imagedest, pos, (uint4){(uint)value,0,0,0});
}



