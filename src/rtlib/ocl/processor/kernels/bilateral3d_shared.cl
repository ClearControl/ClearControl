#define FSIZE 2
#define MAXLOCALSIZE 2001
#define SIGMA 15.f

inline int localIndex(int i, int j, int k,
					  const int locX,const int locY,const int locZ){
  // returns the one dimensional index of arrays representing
  // the local cube values

  i += FSIZE; 
  j += FSIZE; 
  k += FSIZE; 

  return i + locX*j + locX*locY*k;
	  

}

__kernel void run(__read_only image3d_t input, __global short* output,const int Nx, const int Ny, const int Nz)
{
  const sampler_t sampler = CLK_NORMALIZED_COORDS_FALSE |	CLK_ADDRESS_CLAMP_TO_EDGE |	CLK_FILTER_NEAREST ;

  int i = get_global_id(0);
  int j = get_global_id(1);
  int k = get_global_id(2);

  
  const int workSizeX = get_local_size(0);  
  const int workSizeY = get_local_size(1);
  const int workSizeZ = get_local_size(2);
  const int workCubeSize = workSizeX*workSizeY*workSizeZ;

  const int localSizeX = (2*FSIZE + workSizeX);
  const int localSizeY = (2*FSIZE + workSizeY);
  const int localSizeZ = (2*FSIZE + workSizeZ);
  
  const int localCubeSize = localSizeX*localSizeY*localSizeZ;

 
  int iGroup = get_local_id(0);
  int jGroup = get_local_id(1);
  int kGroup = get_local_id(2);
  int indexGroup = iGroup + workSizeX*jGroup + workSizeX*workSizeY*kGroup;

  const int ASTRIDE = ceil(1.f*localCubeSize/workCubeSize);
  

  int4 pos0 = (int4)(get_group_id(0)*workSizeX-FSIZE,
					 get_group_id(1)*workSizeY-FSIZE,
					 get_group_id(2)*workSizeZ-FSIZE,0);


  __local float pixLoc[MAXLOCALSIZE];

  
  for (int k = 0; k < ASTRIDE; ++k){
  	int indexLoc = indexGroup*ASTRIDE+k;
	int tmp = indexLoc;
	int iLoc = tmp % localSizeX;
	tmp = (tmp-iLoc)/localSizeX;
	int jLoc = tmp % localSizeY;
	tmp = (tmp-jLoc)/localSizeY;
	int kLoc = tmp % localSizeZ;
	
	pixLoc[indexLoc] = read_imageui(input,sampler,pos0+(int4)(iLoc,jLoc,kLoc,0)).x;

  }

  
  barrier(CLK_LOCAL_MEM_FENCE);


  float res = 0;
  float sum = 0;


  float val0 = pixLoc[localIndex(iGroup,jGroup,kGroup,
									  localSizeX,localSizeY,localSizeZ)];

  for(int i2 = -FSIZE;i2<=FSIZE;i2++){
    for(int j2 = -FSIZE;j2<=FSIZE;j2++){
  	  for(int k2 = -FSIZE;k2<=FSIZE;k2++){

  		float val1 = pixLoc[localIndex(iGroup+i2,jGroup+j2,kGroup+k2,
									  localSizeX,localSizeY,localSizeZ)];

		float dist = val0-val1;
		
		float weight = exp(-1.f/SIGMA/SIGMA*dist*dist);

		
		res += val1*weight;
  		sum += weight;

  	  }
  	}
  }

  if (i+j*Nx+k*Nx*Ny<Nx*Ny*Nz)
	output[i+j*Nx+k*Nx*Ny] = (short)(res/sum);


  
}
