# this script is used to create data points for the k-means java application via uniform distribution (Gleichverteilung)
# the data is stored in .csv format


n = 100
x = runif(n, min=0, max=1)
y = runif(n, min=0, max=1)
df <- cbind(x,y)

?runif


path <- "D:/Users/Fredy/UNI/BA/git/k-means/points.csv"
write.csv(df, path, row.names=FALSE)
