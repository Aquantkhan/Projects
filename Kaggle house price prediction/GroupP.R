library(ggplot2)
library(psych)
library(caret)
library(Rmisc)
library(xgboost)
library(Ckmeans.1d.dp)
library(naniar)
library(scales)

train = read.csv("train.csv", stringsAsFactors = F)
test = read.csv("test.csv", stringsAsFactors = F)
test_labels <- test$Id
test$Id <- NULL
train$Id <- NULL
test$SalePrice <- NA
HousePrice <- rbind.fill(train, test)

ggplot(data = HousePrice[!is.na(HousePrice$SalePrice),], aes(x = SalePrice)) +
  geom_histogram(col = 'white',
                 fill = 'gold',
                 binwidth = 10000) +
  scale_x_continuous(breaks = seq(0, 800000, by = 100000), labels = comma)


ggplot(train, aes(x = log(SalePrice + 1))) +
  geom_histogram(col = 'white', fill = 'gold') +
  theme_light()


DataIsNum <- which(sapply(HousePrice, is.numeric))
NumericPredictor <- names(DataIsNum)
HousePrice_numVar <- HousePrice[, DataIsNum]


gg_miss_var(HousePrice) + labs(y = "Look at all the missing ones")
MissingData <- which(colSums(is.na(HousePrice)) > 0)
MissingData <- colSums(sapply(HousePrice[MissingData], is.na))
sort(MissingData, decreasing = TRUE)

HousePrice$PoolQC[is.na(HousePrice$PoolQC)] <- 'None'
QualityLevel <-
  c(
    'None' = 0,
    'Po' = 1,
    'Fa' = 2,
    'TA' = 3,
    'Gd' = 4,
    'Ex' = 5
  )
HousePrice$PoolQC <-
  as.integer(revalue(HousePrice$PoolQC, QualityLevel))
table(HousePrice$PoolQC)

HousePrice[HousePrice$PoolArea > 0 &
             HousePrice$PoolQC == 0, c('PoolArea', 'PoolQC', 'OverallQual')]
HousePrice$PoolQC[2421] <- 4
HousePrice$PoolQC[2504] <- 4
HousePrice$PoolQC[2600] <- 4



HousePrice$MiscFeature[is.na(HousePrice$MiscFeature)] <- 'None'
HousePrice$MiscFeature <- as.factor(HousePrice$MiscFeature)




HousePrice$Alley[is.na(HousePrice$Alley)] <- 'None'
HousePrice$Alley <- as.factor(HousePrice$Alley)



HousePrice$Fence[is.na(HousePrice$Fence)] <- 'None'
HousePrice$Fence <- as.factor(HousePrice$Fence)



HousePrice$FireplaceQu[is.na(HousePrice$FireplaceQu)] <- 'None'
HousePrice$FireplaceQu <-
  as.integer(revalue(HousePrice$FireplaceQu, QualityLevel))



for (i in 1:nrow(HousePrice)) {
  if (is.na(HousePrice$LotFrontage[i])) {
    HousePrice$LotFrontage[i] <-
      as.integer(median(HousePrice$LotFrontage[HousePrice$Neighborhood == HousePrice$Neighborhood[i]], na.rm =
                          TRUE))
  }
}

HousePrice$LotShape <-
  as.integer(revalue(HousePrice$LotShape, c(
    'IR3' = 0,
    'IR2' = 1,
    'IR1' = 2,
    'Reg' = 3
  )))

HousePrice$LotConfig <- as.factor(HousePrice$LotConfig)


HousePrice$GarageYrBlt[is.na(HousePrice$GarageYrBlt)] <-
  HousePrice$YearBuilt[is.na(HousePrice$GarageYrBlt)]

HousePrice$GarageQual[2127] <- 'TA'
HousePrice$GarageFinish[2127] <- 'Unf'
HousePrice$GarageCond[2127] <- 'TA'

HousePrice$GarageArea[2577] <- 0
HousePrice$GarageCars[2577] <- 0
HousePrice$GarageType[2577] <- NA



HousePrice$GarageType[is.na(HousePrice$GarageType)] <- 'No Garage'
HousePrice$GarageType <- as.factor(HousePrice$GarageType)


HousePrice$GarageFinish[is.na(HousePrice$GarageFinish)] <- 'None'
Finish <- c(
  'None' = 0,
  'Unf' = 1,
  'RFn' = 2,
  'Fin' = 3
)

HousePrice$GarageFinish <-
  as.integer(revalue(HousePrice$GarageFinish, Finish))




HousePrice$GarageQual[is.na(HousePrice$GarageQual)] <- 'None'
HousePrice$GarageQual <-
  as.integer(revalue(HousePrice$GarageQual, QualityLevel))



HousePrice$GarageCond[is.na(HousePrice$GarageCond)] <- 'None'
HousePrice$GarageCond <-
  as.integer(revalue(HousePrice$GarageCond, QualityLevel))




HousePrice[!is.na(HousePrice$BsmtFinType1) &
             (
               is.na(HousePrice$BsmtCond) |
                 is.na(HousePrice$BsmtQual) |
                 is.na(HousePrice$BsmtExposure) |
                 is.na(HousePrice$BsmtFinType2)
             ), c('BsmtQual',
                  'BsmtCond',
                  'BsmtExposure',
                  'BsmtFinType1',
                  'BsmtFinType2')]
HousePrice$BsmtFinType2[333] <-  'Unf'
HousePrice$BsmtExposure[c(949, 1488, 2349)] <- 'No'
HousePrice$BsmtCond[c(2041, 2186, 2525)] <- 'TA'
HousePrice$BsmtQual[c(2218, 2219)] <- 'TA'


HousePrice$BsmtQual[is.na(HousePrice$BsmtQual)] <- 'None'
HousePrice$BsmtQual <-
  as.integer(revalue(HousePrice$BsmtQual, QualityLevel))

HousePrice$BsmtCond[is.na(HousePrice$BsmtCond)] <- 'None'
HousePrice$BsmtCond <-
  as.integer(revalue(HousePrice$BsmtCond, QualityLevel))

HousePrice$BsmtExposure[is.na(HousePrice$BsmtExposure)] <- 'None'
BsmtExposure <- c(
  'None' = 0,
  'No' = 1,
  'Mn' = 2,
  'Av' = 3,
  'Gd' = 4
)

HousePrice$BsmtExposure <-
  as.integer(revalue(HousePrice$BsmtExposure, BsmtExposure))


HousePrice$BsmtFinType1[is.na(HousePrice$BsmtFinType1)] <- 'None'
BsmtFinType1 <-
  c(
    'None' = 0,
    'Unf' = 1,
    'LwQ' = 2,
    'Rec' = 3,
    'BLQ' = 4,
    'ALQ' = 5,
    'GLQ' = 6
  )
HousePrice$BsmtFinType1 <-
  as.integer(revalue(HousePrice$BsmtFinType1, BsmtFinType1))


HousePrice$BsmtFinType2[is.na(HousePrice$BsmtFinType2)] <- 'None'
BsmtFinType2 <-
  c(
    'None' = 0,
    'Unf' = 1,
    'LwQ' = 2,
    'Rec' = 3,
    'BLQ' = 4,
    'ALQ' = 5,
    'GLQ' = 6
  )
HousePrice$BsmtFinType2 <-
  as.integer(revalue(HousePrice$BsmtFinType2, BsmtFinType2))


HousePrice$BsmtHalfBath[is.na(HousePrice$BsmtHalfBath)] <- 0
HousePrice$TotalBsmtSF[is.na(HousePrice$TotalBsmtSF)] <- 0
HousePrice$BsmtFullBath[is.na(HousePrice$BsmtFullBath)] <- 0
HousePrice$BsmtUnfSF[is.na(HousePrice$BsmtUnfSF)] <- 0
HousePrice$BsmtFinSF1[is.na(HousePrice$BsmtFinSF1)] <- 0
HousePrice$BsmtFinSF2[is.na(HousePrice$BsmtFinSF2)] <- 0



HousePrice[is.na(HousePrice$MasVnrType) &
             !is.na(HousePrice$MasVnrArea), c('MasVnrType', 'MasVnrArea')]
HousePrice$MasVnrType[2611] <- 'BrkFace'

HousePrice$MasVnrType[is.na(HousePrice$MasVnrType)] <- 'None'
MasVnrType <- c(
  'None' = 0,
  'BrkCmn' = 0,
  'BrkFace' = 1,
  'Stone' = 2
)
HousePrice$MasVnrType <-
  as.integer(revalue(HousePrice$MasVnrType, MasVnrType))

HousePrice$MasVnrArea[is.na(HousePrice$MasVnrArea)] <- 0
HousePrice$MSZoning[is.na(HousePrice$MSZoning)] <- 'RL'
HousePrice$MSZoning <- as.factor(HousePrice$MSZoning)


HousePrice$KitchenQual[is.na(HousePrice$KitchenQual)] <- 'TA'
HousePrice$KitchenQual <-
  as.integer(revalue(HousePrice$KitchenQual, QualityLevel))




table(HousePrice$Utilities)
HousePrice$Utilities <- NULL




table(HousePrice$Functional)
HousePrice$Functional[is.na(HousePrice$Functional)] <- 'Typ'
HousePrice$Functional <- as.integer(revalue(
  HousePrice$Functional,
  c(
    'Sal' = 0,
    'Sev' = 1,
    'Maj2' = 2,
    'Maj1' = 3,
    'Mod' = 4,
    'Min2' = 5,
    'Min1' = 6,
    'Typ' = 7
  )
))

HousePrice$Exterior2nd[is.na(HousePrice$Exterior2nd)] <- 'VinylSd'
HousePrice$Exterior2nd <- as.factor(HousePrice$Exterior2nd)


HousePrice$ExterCond <-
  as.integer(revalue(HousePrice$ExterCond, QualityLevel))



table(HousePrice$Exterior1st)
HousePrice$Exterior1st[is.na(HousePrice$Exterior1st)] <- 'VinylSd'
HousePrice$Exterior1st <- as.factor(HousePrice$Exterior1st)


HousePrice$ExterQual <-
  as.integer(revalue(HousePrice$ExterQual, QualityLevel))


table(HousePrice$Electrical)
HousePrice$Electrical[is.na(HousePrice$Electrical)] <- 'SBrkr'
HousePrice$Electrical <- as.factor(HousePrice$Electrical)

table(HousePrice$SaleType)
HousePrice$SaleType[is.na(HousePrice$SaleType)] <- 'WD'
HousePrice$SaleType <- as.factor(HousePrice$SaleType)


HousePrice$SaleCondition <- as.factor(HousePrice$SaleCondition)



Character_Var <-
  names(HousePrice[, sapply(HousePrice, is.character)])


HousePrice$Foundation <- as.factor(HousePrice$Foundation)

HousePrice$HeatingQC <-
  as.integer(revalue(HousePrice$HeatingQC, QualityLevel))

HousePrice$Heating <- as.factor(HousePrice$Heating)
HousePrice$CentralAir <-
  as.integer(revalue(HousePrice$CentralAir, c('N' = 0, 'Y' = 1)))




HousePrice$Condition1 <- as.factor(HousePrice$Condition1)
HousePrice$RoofMatl <- as.factor(HousePrice$RoofMatl)
HousePrice$RoofStyle <- as.factor(HousePrice$RoofStyle)


HousePrice$Condition2 <- as.factor(HousePrice$Condition2)

HousePrice$LandContour <- as.factor(HousePrice$LandContour)

HousePrice$LandSlope <-
  as.integer(revalue(HousePrice$LandSlope, c(
    'Sev' = 0,
    'Mod' = 1,
    'Gtl' = 2
  )))

HousePrice$BldgType <- as.factor(HousePrice$BldgType)

HousePrice$HouseStyle <- as.factor(HousePrice$HouseStyle)

HousePrice$Neighborhood <- as.factor(HousePrice$Neighborhood)

HousePrice$Street <-
  as.integer(revalue(HousePrice$Street, c('Grvl' = 0, 'Pave' = 1)))


HousePrice$PavedDrive <-
  as.integer(revalue(HousePrice$PavedDrive, c(
    'N' = 0, 'P' = 1, 'Y' = 2
  )))


HousePrice$MoSold <- as.factor(HousePrice$MoSold)


HousePrice$MSSubClass <- as.factor(HousePrice$MSSubClass)


HousePrice$MSSubClass <-
  revalue(
    HousePrice$MSSubClass,
    c(
      '20' = '1 story 1946+',
      '30' = '1 story 1945-',
      '40' = '1 story unf attic',
      '45' = '1,5 story unf',
      '50' = '1,5 story fin',
      '60' = '2 story 1946+',
      '70' = '2 story 1945-',
      '75' = '2,5 story all ages',
      '80' = 'split/multi level',
      '85' = 'split foyer',
      '90' = 'duplex all style/age',
      '120' = '1 story PUD 1946+',
      '150' = '1,5 story PUD all',
      '160' = '2 story PUD 1946+',
      '180' = 'PUD multilevel',
      '190' = '2 family conversion'
    )
  )

factorVars <- which(sapply(HousePrice, is.factor))


DataIsNum <- which(sapply(HousePrice, is.numeric))

HousePrice_numVar <- HousePrice[, DataIsNum]


HousePrice$GarageYrBlt[2593] <- 2007

HousePrice$Age <-
  as.numeric(HousePrice$YrSold) - HousePrice$YearRemodAdd



HousePrice$IsNew <-
  ifelse(HousePrice$YrSold == HousePrice$YearBuilt, 1, 0)

HousePrice$YrSold <- as.factor(HousePrice$YrSold)

HousePrice$Remod <-
  ifelse(HousePrice$YearBuilt == HousePrice$YearRemodAdd, 0, 1)

HousePrice$TotalBathroom <-
  HousePrice$FullBath + (HousePrice$HalfBath * 0.5) + HousePrice$BsmtFullBath + (HousePrice$BsmtHalfBath *
                                                                                   0.5)

HousePrice$NeighRichType3 <- c('MeadowV', 'IDOTRR', 'BrDale')
HousePrice$NeighRichType2 <-
  c('MeadowV', 'IDOTRR', 'BrDale', 'StoneBr', 'NridgHt', 'NoRidge')
HousePrice$NeighRichType1 <- c('StoneBr', 'NridgHt', 'NoRidge')



HousePrice$NeighRich[HousePrice$Neighborhood %in% HousePrice$NeighRichType3] <-
  0
HousePrice$NeighRich[!HousePrice$Neighborhood %in% HousePrice$NeighRichType2] <-
  1
HousePrice$NeighRich[HousePrice$Neighborhood %in% HousePrice$NeighRichType1] <-
  2

HousePrice$TotalSqFeet <-
  HousePrice$GrLivArea + HousePrice$TotalBsmtSF


ggplot(data = HousePrice[!is.na(HousePrice$SalePrice), ], aes(x = TotalSqFeet, y =
                                                                SalePrice)) +
  geom_point(shape = 18, color = "blue") +
  geom_smooth(
    method = lm,
    fill = "red",
    aes(fill = SalePrice),
    se = TRUE,
    na.rm = FALSE,
    orientation = NA,
    show.legend = NA,
    inherit.aes = TRUE
  ) +
  scale_y_continuous(breaks = seq(0, 800000, by = 50000),
                     minor_breaks = NULL) +
  labs(title = 'TotalSqFeet correlation with SalePrice')

HousePrice <- HousePrice[-c(524, 1299), ]


HousePrice$TotalPorchSF <-
  HousePrice$OpenPorchSF + HousePrice$EnclosedPorch + HousePrice$X3SsnPorch + HousePrice$ScreenPorch
HousePrice <-
  HousePrice[, !(names(HousePrice) %in% HousePrice$GarageCond)]
HousePrice <-
  HousePrice[, !(names(HousePrice) %in% HousePrice$YearRemodAdd)]
HousePrice <-
  HousePrice[, !(names(HousePrice) %in% HousePrice$TotalRmsAbvGrd)]
HousePrice <-
  HousePrice[, !(names(HousePrice) %in% HousePrice$TotalBsmtSF)]
HousePrice <-
  HousePrice[, !(names(HousePrice) %in% HousePrice$GarageArea)]
HousePrice <-
  HousePrice[, !(names(HousePrice) %in% HousePrice$GarageYrBlt)]
HousePrice <-
  HousePrice[, !(names(HousePrice) %in% HousePrice$BsmtFinSF1)]


LabelEncoded <-
  c('MSSubClass',
    'MoSold',
    'YrSold',
    'SalePrice',
    'OverallQual',
    'OverallCond')
NumericPredictor <-
  NumericPredictor[!(NumericPredictor %in% LabelEncoded)]
numericVarNamesappend <-
  c('Age', 'TotalPorchSF', 'TotBathrooms', 'TotalSqFeet')
NumericPredictor <- append(NumericPredictor, numericVarNamesappend)

DigitData <- HousePrice[, names(HousePrice) %in% NumericPredictor]
OrdinalData <-
  HousePrice[,!(names(HousePrice) %in% NumericPredictor)]
OrdinalData <- OrdinalData[, names(OrdinalData) != 'SalePrice']


for (i in 1:ncol(DigitData)) {
  if (abs(skew(DigitData[, i])) > 0.8) {
    DigitData[, i] <- log(DigitData[, i] + 1)
  }
}

PostProcessing <-
  preProcess(DigitData, method = c("center", "scale"))

DataStandard <- predict(PostProcessing, DigitData)

clone <- as.data.frame(model.matrix( ~ ., OrdinalData))

trial <-
  which(colSums(clone[(nrow(HousePrice[!is.na(HousePrice$SalePrice), ]) +
                         1):nrow(HousePrice), ]) == 0)
colnames(clone[trial])

clone <- clone[, -trial]

Develop <-
  which(colSums(clone[1:nrow(HousePrice[!is.na(HousePrice$SalePrice), ]), ]) == 0)
colnames(clone[Develop])

clone <- clone[, -Develop]

DeficientData <-
  which(colSums(clone[1:nrow(HousePrice[!is.na(HousePrice$SalePrice), ]), ]) < 10)
colnames(clone[DeficientData])

clone <- clone[, -DeficientData]

predictors <- cbind(DataStandard, clone)

HousePrice$SalePrice <- log(HousePrice$SalePrice + 1)

training <- predictors[!is.na(HousePrice$SalePrice), ]
testing <- predictors[is.na(HousePrice$SalePrice), ]


xgb_grid = expand.grid(
  nrounds = c(100, 200, 1000),
  eta = c(0.1, 0.05, 0.01),
  max_depth = c(10, 15, 20, 25),
  gamma = 0,
  colsample_bytree = seq(0.5, 0.9, length.out = 5),
  min_child_weight = 1,
  subsample = 1
)

label_train <- HousePrice$SalePrice[!is.na(HousePrice$SalePrice)]

dtrain <-
  xgb.DMatrix(data = as.matrix(training), label = label_train)
dtest <- xgb.DMatrix(data = as.matrix(testing))

param <- list(
  objective = "reg:squarederror",
  booster = "gbtree",
  eval_metric = "rmse",
  eta = runif(1, .01, .1),
  gamma = 0,
  max_depth = 3,
  min_child_weight = 4,
  subsample = runif(1, .6, .9),
  colsample_bytree = runif(1, .5, .8)
)

xgbcv <- xgb.cv(
  params = param,
  data = dtrain,
  nrounds = 1000,
  nfold = 5,
  showsd = T,
  stratified = T,
  print_every_n = 50,
  verbose = F,
  early_stopping_rounds = 8,
  maximize = FALSE
)

XGBModel  <-xgb.train(data = dtrain,params = param,nrounds = 1000)
XGBpredict <- predict(XGBModel , dtest)
XGBpredict <- exp(XGBpredict)
ImportanceMatrix <-xgb.importance (feature_names = colnames(training), model = XGBModel)
xgb.ggplot.importance(importance_matrix = ImportanceMatrix[1:30], rel_to_first = TRUE)

result <- data.frame(Id = test_labels, SalePrice = (XGBpredict))
head(result)
write.csv(result, file = 'result.csv', row.names = F)
