# lds
**LDS** (Linked Data Similarity) is a JAVA Library for the calculaton of **LOD** based semantic similarity. **LDS** implements similarity measures such as:

* *LDSD:* Linked Data Semantic Distance [1], and its extensions:
   * *TLDSD:* Typeless Linked Data Semantic Distance [2]
   * *WLDSD:* Weighted Linked Data Semantic Distance [2]
   * *WTLDSD:* Weighted Typeless Linked Data Semantic Distance [2]

* *Resim:* Resource Similarity [3], and its extensions:
  * *TResim:* Typeless Resource Similarity [2]
  * *WResim:* Weighted Resource Similarity [2]
  * *WTResim:* Weighted Typeless Resource Similarity [2]

* *LODS:* Linked Open Data Similarity (simI submeasure only) [4]

* *PICSS:* Partitioned Information Content Semantic Similarity [5]

## Getting Started

### Prerequisites
[Apache Maven](https://maven.apache.org/) is needed for the installation and build of the library.

### Installing

``` 
mvn clean install -DskipTests
```
## Running the tests
Tests were created for each implemented similarity measure.

### LDSD measure test

```
mvn test -Dtest=lds.measures.ldsd.LDSDTest
```

### Resim measure test
 
```
mvn test -Dtest=lds.measures.resim.ResimTest
```

### LODS (simI submeasure) test

```
mvn test -Dtest=lds.measures.LODS.SimI_Test
```

### PICSS measure test

```
mvn test -Dtest=lds.measures.picss.PICSSTest
```

## Built With
* [Java](https://www.java.com/download/)
* [Apache Maven](https://maven.apache.org/)
* [Semantic Measure Library](http://www.semantic-measures-library.org)
* [MapDB](http://www.mapdb.org/)
* [Apache Jena](https://jena.apache.org/)

## Licence

## References:
[1] Passant, Alexandre. “Measuring Semantic Distance on Linking Data and Using it for Resources Recommendations.” AAAI Spring Symposium: Linked Data Meets Artificial Intelligence (2010).

[2] Alfarhood, Sultan. “Exploiting Semantic Distance in Linked Open Data for Recommendation.” (2017).

[3] Piao, Guangyuan and John G. Breslin. “Measuring semantic distance for linked open data-enabled recommender systems.” SAC '16 (2016).

[4] Cheniki, Nasredine, Abdelkader Belkhir, Yacine Sam and Nizar Messai. “LODS: A Linked Open Data Based Similarity Measure.” 2016 IEEE 25th International Conference on Enabling Technologies: Infrastructure for Collaborative Enterprises (WETICE) (2016): 229-234.

[5] Meymandpour, Rouzbeh, and Davis, J. G. “A semantic similarity measure for linked data: An information content-based approach.” Knowledge-Based Systems, 109, 276–293. https://doi.org/10.1016/j.knosys.2016.07.012 (2016).

