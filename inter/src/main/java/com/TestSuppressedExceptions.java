package com;

/**
 * Created by andrii on 10.07.16.
 */
public class TestSuppressedExceptions {

    public static void main(String[] args) throws Exception {

        try ( OpenDoor door = new OpenDoor() ) {
            door.swing(); /*this throws a SwingExecption*/
        }
        catch (Exception e) {
            System.out.println("Excepction e:Is there a draft? " + e.getClass());
            System.out.println("SUPPRESSED : " + e.getSuppressed()[0]);
        }
        finally {
            System.out.println("I'm putting a sweater on, regardless. ");
        }
    }
}

class OpenException extends Exception {}
class SwingException extends Exception {}
class CloseException extends Exception {}

class OpenDoor implements AutoCloseable {

    public OpenDoor() throws Exception {
        System.out.println("The door is open.");
    }
    public void swing() throws Exception {
        System.out.println("The door is becoming unhinged.");
        throw new SwingException();
    }

    public void close() throws Exception {
        System.out.println("The door is closed.");
        throw new CloseException(); /* throwing CloseException */
    }
}