typedef struct {
int32	cnt;	 /* number of bytes that follow */
char	str[1];	 /* cnt bytes */
} LStr, *LStrPtr, **LStrHandle;

#define LVBoolean char
#define __cplusplus bla
