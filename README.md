# NetzChatFilter

## Description
This is a simple chat filter for the game "Minecraft". It is written in Java.
It does it by using a filter chain, which is a list of filters that are applied to the chat message in a specific order.

## What String Metric Algorithms should be used?

### Long Story Short
The recommended algorithm is the `Levenshtein Distance` algorithm.
It is the most suitable for this application because it considers the order of characters and provides a clear metric for similarity.

If you don't want to consider the order of characters, you can use the `Jaccard Index` or `Cosine Similarity` algorithms.
They are faster than the `Levenshtein Distance` algorithm.

### BigO Performance

**Jaccard Index:** O(n + m)  
**Cosine Similarity:** O(n + m)  
**Levenshtein Distance:** O(n * m)  
