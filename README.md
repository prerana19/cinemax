
# Cinemax

## Introduction
Cinemax is a comprehensive movie management application that allows users to browse, search, and manage their favorite movies. It provides detailed information about movies, including ratings and reviews.

## Features
- Browse now playing and trending movies
- Search for movies by title
- View detailed information about each movie
- Create and manage a list of favorite movies

## Installation
Install Cinemax on your Android device using the cinemax.apk provided.

## Deeplink
- You can share any movie from the movie details page using the share button. The shared link will open the movie details page in the Cinemax app.
- For opening via adb, execute command `adb shell am start -W -a android.intent.action.VIEW -d "cinemax://movie/details?id={movieId}" com.inshorts.cinemax`
- Sample movie id = 972533

## App preview
<p align="center">
  <img src="assets/screenshot-Google Pixel 6-12.0.png" width="22%" />
  <img src="assets/screenshot-Google Pixel 6-12.0 (1).png" width="22%" />
  <img src="assets/screenshot-Google Pixel 7-13.0 (5).png" width="22%" />
  <img src="assets/screenshot-Google Pixel 7-13.0 (4).png" width="22%" />
</p>
