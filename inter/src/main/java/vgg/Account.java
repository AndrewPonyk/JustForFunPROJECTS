/*
This class was written by a very junior Java engineer. 
There are at least 10 things wrong with this class. 
We would like you identify all 10 things that are wrong and
refactor the class below into Java 8 compatible code.
Please *EXPLAIN* clearly *WHAT* you are changing.
You will be graded based on CLEAR EXPLANATIONS, not just 
refactoring.
*/

/*
  10 wrong things:
* 1) Returns List, not ArrayList (Returning List will make it possible to use anything that implements a List interface, while using an ArrayList force to use an ArrayList.)
* 2) Use generics where possible (casting is not required if generics are used)
* 3) No logging configured
* 4) I would define Custom exception, and rethrow it in case of errors
* 5) Change equals method( comparing by reference is incorrect)
* 6) Define also hashCode for using in hash data structures
* 7) Java8 features not used
* 8) No validation in constructor (so Account with accountNumber = null can be created)
* 9) Global variable 'i' is created in line 33 (this variable should be local inside loop)
* 10) No validation in 'makeTransactionFromDbRow' method
* */
/*
    Also creation of Objects can be changed,
* */

package vgg;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

public class Account {
//    private String accountNumber;
//    private static final Logger LOGGER = LoggerFactory.getLogger(Account.class);
//
//    public Account(String accountNumber) throws MyCustomException {
//        if (accountNumber == null || "".equals(accountNumber)) {
//            throw new MyCustomException("Wrong accountNumber");
//        }
//        this.accountNumber = accountNumber.trim();
//    }
//
//    public String getAccountNumber() {
//        return accountNumber;
//    }
//
//    public List<Transaction> getTransactions() throws MyCustomException {
//        try {
//            List<DbRow> dbTransactionList = Db.getTransactions(accountNumber); //Get the list of transactions
//            List<Transaction> transactionList = new ArrayList<>();
//            dbTransactionList.forEach(row ->{
//                Transaction trans = null;
//                try {
//                    trans = makeTransactionFromDbRow(row);
//                    transactionList.add(trans);
//                } catch (MyCustomException e) {
//                    LOGGER.error(e.getMessage());
//                }
//
//            });
//
//            return transactionList;
//        } catch (SQLException ex) {
//            String message = "Can't retrieve transactions from the database";
//            LOGGER.error(message);
//            throw new MyCustomException(message);
//        }
//    }
//
//    public Transaction makeTransactionFromDbRow(DbRow row) throws MyCustomException {
//        String description = row.getValueForField("desc");
//        String amt = row.getValueForField("amt");
//        if (!NumberUtils.isNumber(amt)) {
//            LOGGER.error("Parsing exception");
//            throw new MyCustomException("String is not a number " + amt);
//        }
//
//        double currencyAmountInPounds = Double.parseDouble(amt);
//        return new Transaction(description, currencyAmountInPounds);
//    }
//
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        Account account = (Account) o;
//
//        return accountNumber.equals(account.accountNumber);
//
//    }
//
//    @Override
//    public int hashCode() {
//        return accountNumber.hashCode();
//    }
}