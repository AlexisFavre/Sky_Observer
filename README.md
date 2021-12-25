# Sky Observer

Realised by Alexis Favre & Augustin Allard

In this project, we have built a sky observer in which one can determine the exact position of all the stars and planets by entering a longitude, latitude and time zone.
Here is a video of the finished project:

https://user-images.githubusercontent.com/56833126/143923599-eff35da3-b49f-4a2b-9b99-19f22b010c8d.mp4

## Implementation

The project was designed in the main parts. 

1) Model Math concept, class: Preconditions, Interval, ClosedInterval, RightOpenInterval, Angle and Polynomial
2) Model the coordinate system, class: SphericalCoordinates, GeographicCoordinates, HorizontalCoordinates, EquatorialCoordinates et EclipticCoordinates) 
3) Allow easy conversion between the coordinate system, class: SiderealTime, EclipticToEquatorialConversion et EquatorialToHorizontalConversion
4) Model the projection onto the sky and different celestial objets, class: StereographicProjection, CelestialObject, Planet, Moon 
5) Draw the Sky using Java FX class: SkyCanvasPainter
6) Create GUI for the Application, class: ViewingParametersBean, ObserverLocationBean, SkyCanvasManager,Main
