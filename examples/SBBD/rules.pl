match(Ev1,Ev2,SSN):- 
    employee(before,Ev1), employee(after,Ev2), 
    ssn(Ev1,SSN), ssn(Ev2,SSN).

salary_increased(SSN):- 
    match(EmployeeBefore,EmployeeAfter,SSN),
    salary(EmployeeBefore, SalaryBefore ), 
    salary(EmployeeAfter, SalaryAfter), 
    SalaryBefore < SalaryAfter.

promoted(SSN):- 
    match(EmployeeBefore,EmployeeAfter,SSN),  
    job(EmployeeBefore,JobBefore), 
    job(EmployeeAfter,JobAfter), 
    JobBefore \= JobAfter, 
    salary_increased(SSN).

transferred(SSN):- 
    match(EmployeeBefore,EmployeeAfter,SSN),  
    branch(EmployeeBefore,BranchBefore),
    branch(EmployeeAfter,BranchAfter), 
    BranchBefore \= BranchAfter.

fired(SSN):- 
	exists_in_base(SSN), 
	not(exists_in_modified(SSN)), 
	ssn(Fb,SSN).

hired(SSN):- 
	exists_in_modified(SSN), 
	not(exists_in_base(SSN)), 
	ssn(Fa,SSN).

department_changed(SSN):- 
	match(EmployeeBefore,EmployeeAfter,SSN), 
	department(EmployeeBefore,DepartamentBefore), 
	department(EmployeeAfter,DepartamentAfter),
	DepartamentBefore \= DepartamentAfter, 
	ssn(EmployeeBefore,SSN).
	
exists_in_modified(SSN):- 
	employee(after,Fa), ssn(Fa,SSN).

exists_in_base(SSN):- 
	employee(before,Fb), ssn(Fb,SSN).

diff(SalInc,Tranf,Prom,Fired,Hired,DeptChg):- 
	findall(N1, (match(Eb1,Ea1,SSN1), salary_increased(SSN1), name(Eb1,N1)), SalInc), 
	findall(N2, (match(Eb2,Ea2,SSN2), transferred(SSN2), name(Eb2,N2)), Tranf),
	findall(N3, (match(Eb3,Ea3,SSN3),  promoted(SSN3), name(Eb3,N3)), Prom),
	findall(N4, (employee(before,Eb4), ssn(Eb4,SSN4), 
	fired(SSN4), name(Eb4,N4)), Fired), 
	findall(N5, (employee(after,Ea5), ssn(Ea5,SSN5),
	hired(SSN5), name(Ea5,N5)), Hired),
	findall(N6, (match(Eb6,Ea6,SSN6), department_changed(SSN6) , name(Eb6,N6)), DeptChg).