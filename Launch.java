package version2;

public class Launch {
    public static void main(String[] args) {
        // instantiate ACM
        ACM acm = new ACM();

        // create test subjects
        acm.addSubject("root","password","adm1","abc", Role.ADMINISTRATOR);
        acm.addSubject("adm1", "abc", "usr1", "abc", Role.REGULAR_USER);
        acm.addSubject("adm1","abc","sec1","abc", Role.SECURITY_OFFICER);

        // create test objects
        acm.addObject("adm1","abc","tbl1");
        acm.addObject("adm1","abc","tbl2");

        // grant test
        acm.grantObjectCommand("adm1", "abc", "usr1", "tbl1", Commands.SELECT);

        // revoke test
        acm.revokeObjectRights("adm1", "abc", "usr1", "tbl1", Commands.SELECT);

        // transfer test
        acm.addSubject("root","password","adm2","abc", Role.ADMINISTRATOR);
        acm.transferObjectOwnership("adm1","abc","adm2","tbl2");

        // subject removal test
        acm.addSubject("adm2","abc","usr2","abc", Role.REGULAR_USER);
        acm.removeSubject("adm2", "abc", "usr2");

        // object removal test
        acm.addObject("adm1","abc","tbl3");
        acm.removeObject("adm1","abc","tbl3");

        // object command test- authorized user
        System.out.println(acm.executeObjectCommand("adm2","abc","tbl2",Commands.SELECT));

        // object command test- unauthorized user
        System.out.println(acm.executeObjectCommand("usr1","abc","tbl2",Commands.SELECT));

        // tcl test- authorized user attempts commit
        System.out.println(acm.executeTCLCommand("sec1", "abc",true));

        // tcl test- un-authorized user attempts rollback
        System.out.println(acm.executeTCLCommand("usr1", "abc",false));

        // try to grant to someone that doesnt exist
        acm.grantObjectCommand("adm1","abc","nope1", "tbl1");

        // print out ACM
        acm.printObjectACM();
        acm.printSubjectACM();
    }
}
