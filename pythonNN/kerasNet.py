import keras
import numpy
import os
import pickle
from keras.layers import Dense, regularizers, Dropout
from keras.models import Sequential
from math import fabs
from random import randint


class TrainUnit:
    def __init__(self, s, a, r, sa):
        self.s = s
        self.a = a
        self.r = r
        self.sa = sa

    def disolve(self):
        return self.s, self.a, self.r, self.sa


class KerasNN:
    kerasPath = "NN.h5"
    LEARNING_FACTOR = 0.99
    before_save = 1000
    save_name = "weights"

    def __init__(self):

        self.dataSet = []
        if os.path.isfile('dataset.pkl'):
            with open('dataset.pkl', 'rb') as input:
                self.dataSet = pickle.load(input)

        self.model = Sequential()
        self.model.add(Dense(1000, kernel_initializer="uniform", activation='relu', input_dim=86,
                             kernel_regularizer=regularizers.l2(0.1), activity_regularizer=regularizers.l1(0.1)))
        self.model.add(Dropout(0.1))
        self.model.add(Dense(1000, kernel_initializer="uniform", activation='relu',
                             kernel_regularizer=regularizers.l2(0.1), activity_regularizer=regularizers.l1(0.1)))
        self.model.add(Dropout(0.1))
        self.model.add(Dense(500, kernel_initializer="uniform", activation='relu',
                             kernel_regularizer=regularizers.l2(0.1), activity_regularizer=regularizers.l1(0.1)))
        self.model.add(Dropout(0.1))
        self.model.add(Dense(57, kernel_initializer="uniform", activation='linear'))

        optimizer = keras.optimizers.RMSprop(lr=0.0001)
        self.model.compile(loss='mse',
                           optimizer=optimizer)
        try:
            self.model.load_weights('{}.h5'.format(self.save_name))
            print(
                "loading from {}.h5".format(self.save_name))
        except:
            print(
                "Training a new model")

    def add(self, s, a, r, sa):
        self.dataSet.append(TrainUnit(s, a, r, sa))

        if len(self.dataSet) % 10000 == 0:
            with open('dataset.pkl', 'wb') as output:
                pickle.dump(self.dataSet, output, pickle.HIGHEST_PROTOCOL)

    def classify(self, s):
        s2 = numpy.array([s])
        q = self.model.predict(s2, batch_size=1)
        answer = q.tolist()[0]
        if fabs(answer[5]) > 1000.0:
            print("dang it {}".format(answer[5]))
        return answer

    def get_batch(self, size):
        answer = []
        counter = 0
        if size < len(self.dataSet):
            while len(answer) < size:
                unit = self.dataSet[randint(0, len(self.dataSet) - 1)]
                if unit.a == 56 and counter < 4:
                    counter += 1
                    answer.append(unit)
                if unit.a != 56:
                    answer.append(unit)
        return answer

    def learn(self):
        self.before_save -= 1
        if self.before_save < 0:
            self.model.save(self.kerasPath)
            self.model.save_weights("weights.h5")
            self.before_save = 10000
            print("Saved with %d" % len(self.dataSet))

        batch = self.get_batch(64)
        for unit in batch:
            s, a, r, sa = unit.disolve()
            qs = self.model.predict(numpy.array([s]), batch_size=1).tolist()[0]
            maxQsa = max(self.model.predict(numpy.array([sa])).tolist()[0])
            # if fabs(r) >= 200.0:
            #     qs[a] = r
            # else:
            qs[a] = r + self.LEARNING_FACTOR * (maxQsa)
            self.model.fit(numpy.array([s]), numpy.array([qs]), epochs=1, verbose=0, batch_size=1)
