package version2;

public class Launch {
    public static void main(String[] args) {
        ACM acm = new ACM();

        acm.addSubject("root","password","adm1","abc", Role.ADMINISTRATOR);
        acm.addObject("root","password","tbl1");
        acm.addObject("root","password","tbl2");
        acm.addObject("root","password","tbl3");

        acm.printObjectACM();
        acm.printSubjectACM();
    }
}
