# Laboratory Work 1: Taylor Series for Sine Function

## Project Description

This is a Java console application that calculates the sine function using Taylor series. The project shows two different ways to do the calculations:
- Using simple data types (double)
- Using BigDecimal and BigInteger classes for high precision

## Project Goals

- Implement Taylor series calculation for sin(x) function
- Compare calculation accuracy between our algorithm and standard Math.sin() function
- Learn to work with BigDecimal, BigInteger, Formatter, Scanner, and BufferedReader classes
- Practice data formatting and output

  
## Main Features

### What the program can do:
- **Calculate sin(x)** using Taylor series with given precision
- **Two implementations**:
  - `double` version - fast calculations with normal precision
  - `BigDecimal` version - calculations with high precision
- **Compare results** with standard Math.sin() function
- **Formatted output** in different number systems

### Special features:
- Calculations stop when precision reaches `e = 10^(-k)`
- Optimized calculation (no refactoring of factorial and power)
- Handles angles greater than 360° correctly
- Works with negative angles

