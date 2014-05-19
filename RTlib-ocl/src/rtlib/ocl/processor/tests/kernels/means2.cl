#define FSIZE 1
#define NSIZE 1
#define BSIZE (FSIZE+NSIZE)

__kernel void run(__read_only image3d_t input1,__read_only image3d_t input2, __global short* output,const int Nx, const int Ny, const int Nz)
{
   
  const float SIGMA = 10.f; 
  
  const sampler_t sampler = CLK_NORMALIZED_COORDS_FALSE |	CLK_ADDRESS_CLAMP_TO_EDGE |	CLK_FILTER_NEAREST ;

  uint i = get_global_id(0);
  uint j = get_global_id(1);
  uint k = get_global_id(2);

  int pix0 = (int)(read_imageui(input1,sampler,(int4)(i,j,k,0)).x);
  
  float res = 0;
  float sum = 0;

  float pixels1[2*FSIZE+1][2*FSIZE+1][2*FSIZE+1];
  float pixels2[2*BSIZE+1][2*BSIZE+1][2*BSIZE+1];

 
  for(int i2 = -FSIZE;i2<=FSIZE;i2++){
    for(int j2 = -FSIZE;j2<=FSIZE;j2++){
      for(int k2 = -FSIZE;k2<=FSIZE;k2++){
	    
	float p1 = (float)(read_imageui(input1,sampler,(int4)(i+i2,j+j2,k+k2,0)).x);
	pixels1[FSIZE+i2][FSIZE+j2][FSIZE+k2] = p1;
      }
    }
  }

  for(int i2 = -BSIZE;i2<=BSIZE;i2++){
    for(int j2 = -BSIZE;j2<=BSIZE;j2++){
      for(int k2 = -BSIZE;k2<=BSIZE;k2++){
	float p1 = (float)(read_imageui(input2,sampler,(int4)(i+i2,j+j2,k+k2,0)).x);
	pixels2[BSIZE+i2][BSIZE+j2][BSIZE+k2] = p1;
      }
    }
  }

  

  
  for(int i2 = -NSIZE;i2<=NSIZE;i2++){
    for(int j2 = -NSIZE;j2<=NSIZE;j2++){
  	  for(int k2 = -NSIZE;k2<=NSIZE;k2++){

	    float dist = 0.f;
	    
	    for(int i3 = -FSIZE;i3<=FSIZE;i3++){
	      for(int j3 = -FSIZE;j3<=FSIZE;j3++){
	    	for(int k3 = -FSIZE;k3<=FSIZE;k3++){

	    	  float p1 = pixels1[FSIZE+i3][FSIZE+j3][FSIZE+k3];
		  float p2 = pixels2[BSIZE+i2+i3][BSIZE+j2+j3][BSIZE+k2+k3];	  
	    	  dist += .1f*(p1-p2)*(p1-p2);
  	    	}
	      }
	    }

	    
	    int pix1 = read_imageui(input2,sampler,(int4)(i+i2,j+j2,k+k2,0)).x;

	    float weight = exp(-1.f/SIGMA/SIGMA*dist);

	    res += pix1*weight;
	    sum += weight;

  	  }
    }
  }


  output[i+j*Nx+k*Nx*Ny] = (short)(res/sum);

  
}

