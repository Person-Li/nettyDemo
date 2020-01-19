import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class test {
    public static void main(String[] args) {
        try {
            Object o=Class.forName("test2").newInstance();
            List<Object> arr=new ArrayList<>();
            arr.add(o);
            Object a=arr.get(0);
            a.getClass().getMethod("test2Fun").invoke(a,null);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
