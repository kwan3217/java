package csg;

class Stack
{
  public  String    stack[];
  public  int       top = -1;

  Stack()
  {
    stack = new String[100];
  }

  public void push(String tobepushed)
  {
    stack[++top] = tobepushed;
  }

  public String pop(int und[])
  {
    if(top == -1)
     {
      und[0] = 1;
      return(null);
    }
    und[0] = 0;
    return( stack[top--]);
  }

}
