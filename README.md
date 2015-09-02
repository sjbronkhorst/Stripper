# Stripper


**Strip (noun) - A long narrow piece, usually of uniform width** 
**Stripper (noun) - A person or thing that breaks something into strips**.

###Hello newcomer, welcome to my thesis project - Stripper.
===

Stripper is a package used to analyse deflection, stresses and buckling modes of beams.
In mathematical terms it is a model based on the Finite Strip Method (FSM) developed by Y. K. Cheung in 1976.

*How it works :*

1. User makes a model by specifying its length, boundary conditions and amount of Fourier terms (more on these later).
2. User makes a cross-section for the model by specifying node coordinates and lines (strips) between nodes.
3. User adds loads to the model.
4. The model is analysed and the deflection at each node is returned.

##Model :

A model consists of a cross-section , boundary-conditions and a length.

##Cross-section :

A cross section consists of Nodes and lines connecting these nodes called Strips.

A typical Cross-section consisting of 4 Nodes and 3 Strips : 

O+++++++++++++++O

+

+

+

+

+

O+++++++++++++++O

O = Node

+++ = Strip


A Model with the above cross section and a length would look like this:

![alt text](http://docs.sketchup.engineeringtoolbox.com/components/42/bs-4_rolled_steel_channels_large.png)

## Boundary conditions :

Boundary conditions represent the condtions at the ends of the beam. The end of a beam may be Simply-Supported (S), Clamped (C) or Free(F).

For example: The boundary conditions of a beam with both ends Clamped would be denoted as (C-C), and in the case of one end clamped and the other free - (C-F) 

###*What is meant by Clamped, Free and simply supported?*

The left end of this beam is clamped while the right is free (C-F) : 

![alt text](http://www.geom.uiuc.edu/education/calc-init/static-beam/img/cantilevered.gif)

Both ends of this beam are simply-supported (S-S) :

![alt text](http://www.leancrew.com/all-this/images/simple-simple.png)

## Fourier series :

In FSM the boundary condtions of a model is determined by its Fourier series.

Take the following Fourier series on the domain y=0 to y=a as an example:

Y(y) = sin(u.y/a) , (u = pi, 2.pi,3.pi, ... , m.pi)

This Fourier series has 2 characteristics worth mentioning:

1. The value of all terms are zero at y=0 and y=a.
2. The first derivative is non-zero at y=0 and y=a.

In fact it represents all possible bending shapes of a beam that is simply-supported at both ends!

1. The deflection can only be zero at both ends.
2. The rotation is non-zero at both ends.

So: By choosing a Fourier series with the correct shape we choose our boundary conditions.





