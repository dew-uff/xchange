
salary_increased(SSN):-
    employee(before,EMPLOYEEBefore),
    employee(after,EMPLOYEEAfter),
    id(EMPLOYEEBefore,ID),
    id(EMPLOYEEAfter,ID),
    ssn(EMPLOYEEBefore,SSN),
    salary(EMPLOYEEBefore,SALARYBefore),
    salary(EMPLOYEEAfter,SALARYAfter),
    SALARYBefore<SALARYAfter.

promoted(SSN):-
    employee(before,EMPLOYEEBefore),
    employee(after,EMPLOYEEAfter),
    id(EMPLOYEEBefore,ID),
    id(EMPLOYEEAfter,ID),
    ssn(EMPLOYEEBefore,SSN),
    job(EMPLOYEEBefore,JOBBefore),
    job(EMPLOYEEAfter,JOBAfter),
    JOBBefore\=JOBAfter,salary(EMPLOYEEBefore,SALARYBefore),
    salary(EMPLOYEEAfter,SALARYAfter),
    SALARYBefore<SALARYAfter.

transferred(SSN):-
    employee(before,EMPLOYEEBefore),
    employee(after,EMPLOYEEAfter),
    id(EMPLOYEEBefore,ID),
    id(EMPLOYEEAfter,ID),
    ssn(EMPLOYEEBefore,SSN),
    branch(EMPLOYEEBefore,BRANCHBefore),
    branch(EMPLOYEEAfter,BRANCHAfter),
    BRANCHBefore\=BRANCHAfter.

fired(SSN):-
    employee(after,EMPLOYEEAfter),
    id(EMPLOYEEAfter,ID),
    ssn(EMPLOYEEAfter,SSN),
    not((employee(before,EMPLOYEEBefore),
    id(EMPLOYEEBefore,ID))).

hired(SSN):-
    employee(before,EMPLOYEEBefore),
    id(EMPLOYEEBefore,ID),
    ssn(EMPLOYEEBefore,SSN),
    not((employee(after,EMPLOYEEAfter),
    id(EMPLOYEEAfter,ID))).

department_changed(SSN):-
    employee(before,EMPLOYEEBefore),
    employee(after,EMPLOYEEAfter),
    id(EMPLOYEEBefore,ID),
    id(EMPLOYEEAfter,ID),
    ssn(EMPLOYEEBefore,SSN),
    department(EMPLOYEEBefore,DEPARTMENTBefore),
    department(EMPLOYEEAfter,DEPARTMENTAfter),
    DEPARTMENTBefore\=DEPARTMENTAfter.
