/**
 * Colossal Airlines Final Project
 * 
 * My Passenger and flight class.
 * 
 * @author Thomas Scanlan 
 * @version 8/20/14
 */

import java.io.Serializable;


class Passenger implements Serializable {
    
    private String firstname;
    private String lastname;
    private String address;
    private String city;
    private String state;
    private String zipcode;
    private String flightNum;
    private int seatNumber;
    
    Passenger(String firstname, String lastname, String address, String city,
            String state, String zipcode, String flightNum, int seatNumber) 
    {
        this.firstname = firstname;
        this.lastname = lastname;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zipcode = zipcode;
        this.flightNum = flightNum;
        this.seatNumber = seatNumber;
    } 

    Passenger() 
    { 
        // Default constructor
    }
    
    String getLastName() {
        return lastname;
    }
    
    String getFirstName() {
        return firstname;
    }
    
    int getSeatNumber() {
        return seatNumber;
    }
    
    String getFlightNum() {
        return flightNum;
    }
      
    void setFlightNum(String flightNum) {
	this.flightNum = flightNum;
    }

    void setLastName(String lastname) {
	this.lastname = lastname;
    }

    void setFirstName(String firstname) {
	this.firstname = firstname;
    }

    void setSeatNum(int seatNumber) {
	this.seatNumber = seatNumber;
    }

} // END OF PASSENGER CLASS

class Flight implements Serializable {
    
    private Passenger [] seatsOfPassengers;
    private String flightNum;
    
    public Flight(String flightNum) {
        this.flightNum = flightNum;
        seatsOfPassengers = new Passenger[10];
    }
    
    void reserveSeat(String firstname, String lastname, String address, String city,
            String state, String zipcode, String flightNum, int seatNumber)
    {
        seatsOfPassengers[seatNumber-1] = new Passenger(firstname, lastname, address,
                city, state, zipcode, flightNum, seatNumber);
    }
    
    boolean isFlightFull() {
        for (int i = 0; i < seatsOfPassengers.length; i++) {   
            if (seatsOfPassengers[i] == null) 
                    return false;    
        } 
        return true;
    }
    
    boolean isNonSmokingSectionFull() {
        for (int i = 5; i < seatsOfPassengers.length; i++) {
            if (seatsOfPassengers[i] == null)
                return false;
        }
        return true;
    }
     
    boolean isSmokingSectionFull() {
        for (int i = 0; i < 5; i++) {   
            if (seatsOfPassengers[i] == null) 
                    return false;                  
        }
        return true;
    }
    
    boolean isSeatTaken(int seat) {
        if (seatsOfPassengers[seat-1] == null) {
            return false;
        }
        return true;
    }
    
    boolean deletePassenger(String first, String last) {
        
        for (int i = 0; i < seatsOfPassengers.length; i++) {
            if ( seatsOfPassengers[i] != null) {
                if ( seatsOfPassengers[i].getLastName().equalsIgnoreCase(last)  
                     && seatsOfPassengers[i].getFirstName().equalsIgnoreCase(first)) 
                {
                     seatsOfPassengers[i] = null;
                     return true;
                }
            }
        }
        return false;
    }
    
    boolean deletePassenger(int seatNumber) {
        if (this.isSeatTaken(seatNumber)) {
            seatsOfPassengers[seatNumber - 1] = null;
            return true;
        }
        return false;
    }
    
    Passenger getPassenger(int seatNum) {
        return seatsOfPassengers[seatNum-1];
    }
    
    Passenger getPassenger(String first, String last) {
       
        Passenger tempPassenger;
        for (int i = 0; i < seatsOfPassengers.length; i++) {
            if ( seatsOfPassengers[i] != null) {
                if ( seatsOfPassengers[i].getLastName().equalsIgnoreCase(last)  
                     && seatsOfPassengers[i].getFirstName().equalsIgnoreCase(first)) 
                {
                    tempPassenger = seatsOfPassengers[i]; 
                    return tempPassenger;
                }
            }
        }
        return null;
    }

    public Passenger copyPassengerObject(Passenger object) {
        Passenger copyPas = null;
        if (object != null) {
            copyPas = new Passenger();
            copyPas.setFlightNum(object.getFlightNum());
            copyPas.setLastName(object.getLastName());
            copyPas.setFirstName(object.getFirstName());
            copyPas.setSeatNum(object.getSeatNumber());
        }
        return copyPas;
    }

    Passenger [] getCopiedFlightsArray () 
    {
        Passenger [] copyArray;
        
        int logicalSize = 0;
        for (int i = 0; i < seatsOfPassengers.length; i++) {
            if (seatsOfPassengers[i] != null) {
                logicalSize++;
            }
        }
        
        copyArray = new Passenger[logicalSize];
        int j = 0;
        for (int i  = 0; i < seatsOfPassengers.length; i++) {
            if (seatsOfPassengers[i] != null) {
                copyArray[j] = copyPassengerObject(seatsOfPassengers[i]);
                j++;
            }
        }
        return copyArray;
    }
    
    Passenger [] getSortedFlight() {
        
        Passenger [] sortedPas = getCopiedFlightsArray();
        boolean swap = true;
        Passenger temp;
        while(swap) {
            swap = false;
            for (int i = 0; i < sortedPas.length-1; i++) {    
                if (sortedPas[i].getLastName().compareToIgnoreCase(sortedPas[i + 1].getLastName()) > 0 )  {                          
                    temp = sortedPas[i];
                    sortedPas[i] = sortedPas[i+1];
                    sortedPas[i+1] = temp;
                    swap = true;
                }    
                else if (sortedPas[i].getLastName().compareToIgnoreCase(sortedPas[i+1].getLastName()) == 0) {
                    if (sortedPas[i].getFirstName().compareToIgnoreCase(sortedPas[i+1].getFirstName()) > 0) {    
                        temp = sortedPas[i];                         
                        sortedPas[i] = sortedPas[i+1];                         
                        sortedPas[i+1] = temp;
                        swap = true;    
                    }    
                }
            }
        }
        return sortedPas;
    }
    
} // END OF FLIGHT CLASS
