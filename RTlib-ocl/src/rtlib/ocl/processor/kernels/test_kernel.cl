__kernel void run(__global const short* a, __global short* b,const int Nx, const int Ny) 
{
    int i = get_global_id(0);
  	int j = get_global_id(1);
  
  
    b[i+Nx*j] = 10*i+j;
}