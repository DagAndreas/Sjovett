# In2000-Prosjekt
Lag og push alltid til en egen branch. Aldri til main. På den måten holder vi main-branchen fri for bugs.


Steg:
1. git clone [repo]
2. git branch [branch_navn] //uten anførselstegn
3. git checkout [branch_navn]
4. // TO DO: gjør koden
5. git add .
6. git commit -m "[kort beskrivelse av endringene]"
7. git push --set-upstream origin [branch_navn]
8. Åpne https://github.uio.no/dafolvel/In2000-Prosjekt
9. trykk på "Pull requests"
10. "New Pull request"
11. Base: main <- Compare :[branch_navn]
12. Create pull request

13. Skriv kommentar av endringene du gjorde
14. Assign deg og de som gjorde enrdingene
15. Reviewers for de som skal se gjennom og godkjenne
16. Create pull request
17. Ta en paus
