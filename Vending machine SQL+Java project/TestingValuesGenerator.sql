
INSERT INTO Supplier VALUES (1, 'The Coca-Cola Company');
INSERT INTO Supplier VALUES (2, 'Nestle');
INSERT INTO Supplier VALUES (3, 'Nabisco');
INSERT INTO Supplier VALUES (4, 'VITASOY International Holdings Limited');
INSERT INTO Supplier VALUES (5, 'PepsiCo');

INSERT INTO Item VALUES (1, 'Cola Drink' , 1, 10);
INSERT INTO Item VALUES (2, 'Cola Drink' , 5, 9.8);
INSERT INTO Item VALUES (3, 'Oreo' , 3, 6.4);
INSERT INTO Item VALUES (4, 'Lemon Tea', 4, 6);
INSERT INTO Item VALUES (5, 'Soy Milk', 4, 8);
INSERT INTO Item VALUES (6, 'Barbecue Flavoured Chips', 5, 10.4);
INSERT INTO Item VALUES (7, 'Cream and Onions Flavoured Chips', 5, 10.4);
INSERT INTO Item VALUES (8, 'Orange Juice', 1, 12);
INSERT INTO Item VALUES (9, 'Peach Flavoured Iced Tea', 1, 10.3);
INSERT INTO Item VALUES (10, 'Iced Coffee', 1, 9.4);
INSERT INTO Item VALUES (11, 'Iced Coffee Full Roast', 1, 9.4);
INSERT INTO Item VALUES (12, 'Iced Coffee Rich', 1, 9.4);
INSERT INTO Item VALUES (13, 'Sports Drink', 5, 15);
INSERT INTO Item VALUES (14, 'Cheese Biscuits' , 3, 5.5);
INSERT INTO Item VALUES (15, 'Water', 4, 9999);

INSERT INTO Warehouse VALUES (1, '1 ROAD OF DOOM');
INSERT INTO Warehouse VALUES (2, '69 ROAD OF POTATOES');
INSERT INTO Warehouse VALUES (3, '420 AVE OF CARROTS');

INSERT INTO Vending_Machine VALUES (1, '12 HUNG LAI RD', 8, 1, 'In Service');
INSERT INTO Vending_Machine VALUES (2, '2 CHAI WAN RD', 16, 2, 'In Repair');
INSERT INTO Vending_Machine VALUES (3, '13 CLOUD VIEW RD', 64, 2, 'In Service');
INSERT INTO Vending_Machine VALUES (4, '234 ELETRIC RD', 32, 3, 'In Service');
INSERT INTO Vending_Machine VALUES (5, '34 HONG ON RD', 128, 1, 'Empty');
INSERT INTO Vending_Machine VALUES (6, '65 JAVA RD', 64, 1, 'In Service');
INSERT INTO Vending_Machine VALUES (7, '34 KORNHILL RD', 32, 2, 'In Service');
INSERT INTO Vending_Machine VALUES (8, '12 SHAU KEI WAN RD', 8, 3, 'In Service');
INSERT INTO Vending_Machine VALUES (9, '23 REPULSE BAY RD', 32, 3, 'Out of Order');
INSERT INTO Vending_Machine VALUES (10, '101 MOUNT KELLETT RD', 32, 2, 'In Service');
INSERT INTO Vending_Machine VALUES (11, '1 CHATHAM RD', 16, 1, 'In Service');
INSERT INTO Vending_Machine VALUES (12, '5 KIMBERLEY RD', 8, 2, 'In Service');
INSERT INTO Vending_Machine VALUES (13, '18 KAI FUK RD', 64, 2, 'Empty');
INSERT INTO Vending_Machine VALUES (14, '6 YING WA RD', 32, 1, 'Out of Order');
INSERT INTO Vending_Machine VALUES (15, '25 WARWICK RD', 8, 3, 'In Service');

INSERT INTO Technician VALUES (1, 'Alibek', 1);
INSERT INTO Technician VALUES (2, 'Gaukhar', 2);
INSERT INTO Technician VALUES (4, 'Yang Ke', 2);
INSERT INTO Technician VALUES (3, 'Michael', 3);
INSERT INTO Technician VALUES (5, 'Sollal', 1);

DECLARE 
    Current_Date DATE := DATE '2021-11-25';
    Refill_Invoice_Counter NUMBER := 0;
    Purchase_Invoice_Counter NUMBER := 0;
    
    Item_ID NUMBER;
    Quantity NUMBER;
    Expiration_Date DATE;
    Delivery_Date DATE;
    Warehouse_ID NUMBER;    
    
    Refill_Quantity NUMBER;
    Machine_ID NUMBER;
    Technician_ID NUMBER;
    Refill_Date DATE;
    
    Purchase_Date DATE;
    
    Task_type VARCHAR(20);
    Date_Published DATE;
    Deadline DATE;
    Status VARCHAR(20);
    
    TYPE ArrayThree IS VARRAY(3) OF VARCHAR(20);
    Task_Array ArrayThree := ArrayThree('Repair', 'Restock', 'Cleanup');
    
    TYPE ArrayFour IS VARRAY(4) OF VARCHAR(20);
    Status_ARRAY ArrayFour := ArrayFour('Completed', 'In Progress', 'To be assigned', 'Canceled');
    
    Warehouse_Quantity NUMBER;
    Machine_Quantity NUMBER;
    
BEGIN 
    DBMS_RANDOM.seed (val => 0);
    FOR Delivery_ID IN 1..100 LOOP
        Item_ID := TRUNC(DBMS_RANDOM.VALUE(1, 16));
        Quantity := TRUNC(DBMS_RANDOM.VALUE(1, 100));
        Delivery_Date := TRUNC( Current_Date + dbms_random.value(-365, 365));
        Expiration_Date := TRUNC( Delivery_Date + dbms_random.value(1, 730));
        Warehouse_ID := TRUNC(DBMS_RANDOM.VALUE(1, 4));
        INSERT INTO Delivery_Invoice VALUES (Delivery_ID, Item_ID, Quantity, Delivery_Date, Expiration_Date, Warehouse_ID);
        FOR p IN 1..TRUNC(dbms_random.value(1, 10)) LOOP  
            Refill_Invoice_Counter := Refill_Invoice_Counter + 1;
            Machine_ID := TRUNC(dbms_random.value(1, 16));
            IF (Quantity > 0) THEN
                Refill_Quantity := TRUNC(dbms_random.value(1, Quantity));
                Quantity := Quantity - Refill_Quantity;
                Refill_Date := TRUNC (Delivery_Date + dbms_random.value(1, Expiration_Date - Delivery_Date));
                Technician_ID := TRUNC(dbms_random.value(1, 6));
                INSERT INTO Refill_Invoice VALUES (Refill_Invoice_Counter, Delivery_ID, Machine_ID, Refill_Quantity, Refill_Date, Technician_ID);
                FOR q IN 1..TRUNC(dbms_random.value(1, Refill_Quantity)) LOOP 
                    Purchase_Invoice_Counter := Purchase_Invoice_Counter + 1;
                    Refill_Quantity := Refill_Quantity - 1;
                    Purchase_Date := TRUNC (Refill_Date + dbms_random.value(1, 21));
                    INSERT INTO Purchase_Invoice VALUES (Purchase_Invoice_Counter, Refill_Invoice_Counter, Purchase_Date);
                END LOOP;
                IF (Refill_Quantity > 0) THEN
                    INSERT INTO Vending_Machine_Stock VALUES(Refill_Invoice_Counter, Refill_Quantity);
                END IF;
            END IF;
        END LOOP;
        IF (Quantity > 0) THEN
            INSERT INTO Warehouse_Stock VALUES (Delivery_ID, Quantity);
        END IF;
    END LOOP;

    FOR TASK_ID IN 1..100 LOOP
        Task_type := Task_Array(TRUNC(dbms_random.value(1, 4)));
        Date_Published := TRUNC(Current_Date + dbms_random.value(-365, 365));
        Deadline := TRUNC(Delivery_Date + dbms_random.value(1, 64));
        Status := Status_Array(TRUNC(dbms_random.value(1, 5)));
        Technician_ID := TRUNC(dbms_random.value(1, 6));
        INSERT INTO Task VALUES (TASK_ID, Task_type, Date_Published, Deadline, Status, Technician_ID);
    END LOOP;
END;

