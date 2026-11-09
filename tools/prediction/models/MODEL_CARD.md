# Russian n-gram prototype model card

## Model

- File: `ru_ngram_stage3_th2.bin`
- Size: 5,381,280 bytes
- SHA-256: `ED8E78FB6EBEE590AB57EB94D911BF4F69201F1E24B39105B0647810CE9B102E`
- Format: custom read-only binary, intended for mmap
- Vocabulary: top 50,000 normalized word forms
- Context: unigrams, bigrams, trigrams
- Pruning: n-grams with count >= 2
- Candidates: top 5 per context
- Backoff: trigram → bigram only; no unigram candidate fallback
- Size split: 35,021 bigram contexts / 91,017 records; 170,383 trigram contexts / 285,072 records

## Source

- Corpus: Tatoeba Russian sentences export
- Source URL: `https://downloads.tatoeba.org/exports/per_language/rus/rus_sentences.tsv.bz2`
- Actual input file used: 15,612,138 bytes
- Actual input SHA-256: `FE81D065A2FE60789AB359A6F680E7B623E45D70215E3E877DD8BB1F9BE96C0A`
- License: CC BY 2.0 FR (some Tatoeba sentences are CC0 1.0)
- Attribution: Tatoeba project and its contributors, https://tatoeba.org
- The official index observed during research listed a 15,489,759-byte snapshot dated 26-Sep-2026. The provided file differs in size, so it is treated as a different snapshot; the actual SHA-256 above is authoritative for this model.

## License notice

This model is a statistical derivative of Tatoeba text. It is distributed with the attribution and license notice required by CC BY 2.0 FR. It must not be presented as an official Tatoeba model or as endorsed by Tatoeba.

## Preprocessing

- UTF-8, NFC normalization.
- Lowercase and `ё` → `е` for lookup.
- Cyrillic word tokens; URLs, emails, phone patterns and @mentions removed.
- Sentence boundary-based train/dev/test split.
- Exact normalized duplicates removed.
- Test/dev sentences sharing any 5-word shingle with train were removed to reduce near-duplicate leakage.

## Intended use

Offline next-word candidate generation for Russian in LeanType, only when native/history dictionaries return no candidates. It is not a general text generator and must not be used to invent random words.

## Not yet validated on Android

The reported latency is from the Python mmap prototype on the development machine. Android load time, RSS, thermal behavior and p95 UI latency remain to be measured.
