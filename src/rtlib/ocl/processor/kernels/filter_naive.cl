#define BLOCKSIZE 5

__kernel void run(__global const short* a, __global short* b,const int Nx, const int Ny)
{
    unsigned i = get_global_id(0);
    unsigned j = get_global_id(1);
    unsigned index = i+j*Nx;


    float res = 0;
    float sum = 0.;
    
    int4 nBlock = BLOCKSIZE*(int4)(-1,1,-1,1);
    
    if (i<BLOCKSIZE)
    	nBlock.x = -i;
    if (i+BLOCKSIZE>=Nx)
    	nBlock.y = Nx-i-1;
   
    if (j<BLOCKSIZE)
    	nBlock.z = -j;
    if (j+BLOCKSIZE>=Ny)
    	nBlock.w = Ny-j-1;
    	

    for(int k = nBlock.x;k<=nBlock.y;k++){
    	for(int m = nBlock.z;m<=nBlock.w;m++){

    		int ind = (i+k)+(j+m)*Nx;
    	  float weight = exp(-.0001*(a[index]-a[ind])*(a[index]-a[ind]));
    	  res += a[ind]*weight;
    		sum += weight;
    	
    	}
    }

    b[index] = (short)(res/sum);

}
