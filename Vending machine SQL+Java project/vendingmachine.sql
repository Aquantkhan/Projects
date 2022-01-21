DROP TABLE Vending_Machine_Stock;
DROP TABLE Warehouse_Stock;
DROP TABLE Purchase_Invoice;
DROP TABLE Refill_Invoice;
DROP TABLE Delivery_Invoice;
DROP TABLE Task;
DROP TABLE Technician;
DROP TABLE Vending_Machine;
DROP TABLE Warehouse;
DROP TABLE Item;
DROP TABLE Supplier;

CREATE TABLE Supplier
(Supplier_ID NUMBER PRIMARY KEY,
Supplier_Name VARCHAR(50) UNIQUE NOT NULL);

CREATE TABLE Item
(Item_ID NUMBER PRIMARY KEY,
Item_Name VARCHAR(50) NOT NULL,
Supplier NUMBER REFERENCES Supplier(Supplier_ID),
Sell_Price NUMBER NOT NULL);

CREATE TABLE Warehouse
(Warehouse_ID NUMBER PRIMARY KEY,
Adress VARCHAR(100) NOT NULL);

CREATE TABLE Vending_Machine
(Vending_Machine_ID NUMBER PRIMARY KEY,
Adress VARCHAR(100) NOT NULL,
Number_of_Slots NUMBER,
Warehouse_ID NUMBER REFERENCES Warehouse(Warehouse_ID),
Status VARCHAR(20)
CONSTRAINT chk_Machine_Status CHECK (Status IN ('In Service', 'Out of Order', 'In Repair', 'Empty')) NOT NULL);

CREATE TABLE Technician
(Technician_ID NUMBER PRIMARY KEY,
Technician_Name VARCHAR(20) NOT NULL,
Warehouse_ID NUMBER REFERENCES Warehouse(Warehouse_ID));

CREATE TABLE Delivery_Invoice
(Delivery_ID NUMBER PRIMARY KEY,
Item_ID NUMBER REFERENCES Item(Item_ID) NOT NULL,
Quantity NUMBER NOT NULL,
Delivery_Date DATE NOT NULL,
Expiration_Date DATE NOT NULL,
Warehouse_ID REFERENCES Warehouse(Warehouse_ID));

CREATE TABLE Refill_Invoice
(Refill_ID NUMBER PRIMARY KEY,
Delivery_ID NUMBER REFERENCES Delivery_Invoice(Delivery_ID) NOT NULL,
Machine_ID NUMBER REFERENCES Vending_Machine(Vending_Machine_ID) NOT NULL,
Quantity NUMBER NOT NULL,
Refill_Date DATE NOT NULL,
Technician_ID NUMBER REFERENCES Technician(Technician_ID) NOT NULL);

CREATE TABLE Purchase_Invoice
(Purchase_ID NUMBER PRIMARY KEY,
Refill_ID NUMBER REFERENCES Refill_Invoice(Refill_ID) NOT NULL,
Purchase_Date DATE NOT NULL);

CREATE TABLE Vending_Machine_Stock
(Refill_ID NUMBER REFERENCES Refill_Invoice(Refill_ID),
Quantity NUMBER NOT NULL);

CREATE TABLE Warehouse_Stock
(Delivery_ID NUMBER REFERENCES Delivery_Invoice(Delivery_ID) NOT NULL,
Quantity NUMBER NOT NULL);

CREATE TABLE Task
(Task_ID NUMBER PRIMARY KEY,
Task_type VARCHAR(20) NOT NULL,
Date_Published DATE NOT NULL,
Deadline DATE NOT NULL,
Status VARCHAR(20)
CONSTRAINT chk_Task_Status CHECK (Status IN ('Completed', 'In Progress', 'To be assigned', 'Canceled')) NOT NULL,
Technician_Assigned_ID NUMBER REFERENCES Technician(Technician_ID));
