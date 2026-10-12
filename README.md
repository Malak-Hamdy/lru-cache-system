# LRU Cache System

A Java implementation of a Least Recently Used (LRU) Cache using a custom hash table and a doubly linked list.

## Overview

The cache stores key-value pairs and tracks their usage order. The most recently used entry is kept at the head of the doubly linked list, while the least recently used entry is kept at the tail.

When the cache reaches its capacity, inserting a new entry evicts the least recently used entry.

## Features

- Retrieve values using `get(key)`.
- Insert or update entries using `put(key, value)`.
- Remove entries using `remove(key)`.
- Display entries from most recently used to least recently used using `display()`.
- Automatically evict the least recently used entry when capacity is reached.
- Resolve hash collisions using linear probing.
- Maintain cache recency using a custom doubly linked list.
- Validate behavior with automated tests, including randomized operations.

## Technologies

- Java
- Custom hash table
- Doubly linked list
- Linear probing
- Automated testing

## Project Structure

- `Node.java` — represents a cache entry with a key, value, and linked-list pointers.
- `DoublyLinkedList.java` — manages entry order and recency.
- `HashTable.java` — stores and retrieves entries using a custom hash table.
- `LRUCache.java` — implements cache operations and capacity management.
- `LRUCacheTest.java` — tests cache functionality and edge cases.

## Getting Started

### Requirements

A Java Development Kit (JDK) must be installed.

### Compile

Run the following command in the directory containing the Java files:

```bash
javac HashTable.java Node.java DoublyLinkedList.java LRUCache.java LRUCacheTest.java
```

### Run the tests

```bash
java LRUCacheTest
```

The test suite checks insertion, retrieval, updates, eviction, removal, display order, hash collisions, edge cases, and randomized operations.

## Complexity

Under reasonable hash distribution, the hash-table operations are expected to take O(1) average time. The doubly linked list supports constant-time insertion, removal, and recency updates when the relevant node is known.

- `get(key)` — O(1) average time.
- `put(key, value)` — O(1) average time.
- `remove(key)` — O(1) average time.
- `display()` — O(n) time.

Hash-table operations can take O(n) in the worst case due to collision probing.

## Learning Objectives

This project demonstrates the integration of custom data structures, hash-table collision handling, linked-list manipulation, cache eviction policies, complexity analysis, and automated testing.

## Scope

This is an educational implementation intended to demonstrate data structures and algorithms. It is not designed as a production caching library.
