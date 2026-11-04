package pruebas;

public class HijoLento {
    public static void main(String[] args) throws InterruptedException {
        long segundos = args.length > 0 ? Long.parseLong(args[0]) : 60;
        Thread.sleep(segundos * 1000);
        System.out.println("OK;fin");
    }
}
