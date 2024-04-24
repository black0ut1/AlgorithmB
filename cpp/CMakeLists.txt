cmake_minimum_required(VERSION 3.20)
project(BP_Cpp)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -pthread")

include_directories(${CMAKE_SOURCE_DIR}/include/)

add_subdirectory(src)
add_subdirectory(test)
