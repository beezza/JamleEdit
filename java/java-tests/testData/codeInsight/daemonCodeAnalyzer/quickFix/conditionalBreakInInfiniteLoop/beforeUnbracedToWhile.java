// "Move condition to loop" "true"
class Main {
  public static void main(String[] args) {
    int i = 0;
    for<caret>(;;) {
      if (i >= 14)
        break;
      else
        i++;
    }
  }
}