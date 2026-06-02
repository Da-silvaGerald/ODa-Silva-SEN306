1. How did you achieve functional cohesion? Which routines did you extract?
Functional cohesion means that every statement inside a method is directed toward performing a single, well-defined task. The original code suffered from coincidental/sequential cohesion, cramming validation, math calculations, string formatting, I/O side effects (printing/emailing), and state updates into one method.

I isolated these responsibilities into 5 distinct routines:

validateInputs: Ensures incoming data matches business logic rules.

calculateOrderSum: Solely aggregates the array values.

getDiscountRate: Isolates conditional business logic for discount tier lookups.

buildNotificationMessage: Handles string assembly exclusively.

sendNotifications: Manages external side-effects (console logging and emails).

2. What parameter passing issues did you encounter?
In the original snippet, line 13 attempts to update the customer's balance: d = total; // tries to update d?.

Because Java uses strictly pass-by-value, when primitive data types (like double d) are passed into a method, a local copy of the value is made on the stack frame. Reassigning d = total inside the function scope only shifts the value of that local copy. The moment execution exits processCustomer, the stack frame is popped, and the original variable passed by the caller remains completely unchanged.

3. How would the d update behave differently if the language used pass-by-value-result?
If the language utilized pass-by-value-result (copy-in/copy-out), the behavior would change completely, and line 13 would actually succeed in updating the caller's variable:

At entry (Copy-in): The value of the external argument is copied into the local variable d.

During execution: The method works with this local variable copy, eventually mutating it to equal total on line 13.

At exit (Copy-out): The final value of the local variable d is automatically copied back and overwritten into the memory location of the caller's original variable.

Thus, the external balance variable would have updated successfully upon the function's termination.