# this script is used to create data points for the k-means java application via uniform distribution (Gleichverteilung)
# the data is stored in .csv format


n = 100000000
x = runif(n, min=0, max=1)
y = runif(n, min=0, max=1)
df <- cbind(x,y)

?runif


path <- "D:/Users/Fredy/UNI/BA/git/k-means/points_100000000.csv"
write.table(df, path, row.names=FALSE)