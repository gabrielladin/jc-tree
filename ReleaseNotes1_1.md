# Release Notes for version jc-Tree\_1.1 #
This release fixes bug and makes available and some minor enhacements have been made like addition of **LinkedTree** and **SortedChidlrenTree**.


## Details ##

Fixes:

Fixed [issue 9](https://code.google.com/p/jc-tree/issues/detail?id=9)

Fixed [issue 10](https://code.google.com/p/jc-tree/issues/detail?id=10)

Fixed [issue 11](https://code.google.com/p/jc-tree/issues/detail?id=11)

Fixed [issue 12](https://code.google.com/p/jc-tree/issues/detail?id=12)

Enhancements:
[issue 6](https://code.google.com/p/jc-tree/issues/detail?id=6) partly: Have introduced a LinkedTree. LinkedTree keeps a reference to its parents and a list of its children. This is a more conventional approach of making trees.

[issue 7](https://code.google.com/p/jc-tree/issues/detail?id=7) : toString method now prints the current nodes in the tree in no particular order. A good visual aid during de-bugging

[issue 8](https://code.google.com/p/jc-tree/issues/detail?id=8) : Introduced SortedChildrenTree. This Tree keeps the children in order. The children need to be implementation of Comparable. A constructor with Comparator as an argument has not been added. May be before the final release it would be done