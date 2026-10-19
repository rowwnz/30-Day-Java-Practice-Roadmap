package sms.ui;

import sms.AppContext;
import sms.service.ReportService;

public class ReportMenu extends Menu {

    public ReportMenu(AppContext ctx) {
        super("REPORTS", ctx);
    }

    @Override
    protected String[] options() {
        return new String[]{
                "Student transcript",
                "Class roster with grades and attendance",
                "Student ranking by GWA",
                "School dashboard"
        };
    }

    @Override
    protected void handle(int choice) {
        ReportService reports = ctx.getReportService();
        switch (choice) {
            case 1:
                System.out.print(reports.transcript(askStudentId()));
                break;
            case 2:
                System.out.print(reports.classRoster(askCourseCode()));
                break;
            case 3:
                System.out.print(reports.studentRanking(in.readInt("Show top how many? (1-100): ", 1, 100)));
                break;
            case 4:
                System.out.print(reports.dashboard());
                break;
            default:
                break;
        }
    }
}
