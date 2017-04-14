import numpy
from keras.layers import Dense, Activation
from keras.models import Sequential, load_model
from pathlib import Path
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
    kerasPath = "NN.keras"
    LEARNING_FACTOR = 0.99
    before_save = 10

    def __init__(self):
        self.dataSet = []

        if Path(self.kerasPath).is_file():
            self.model = load_model(self.kerasPath)
        else:
            self.model = Sequential()
            self.model.add(Dense(64, kernel_initializer="uniform", input_dim=86))
            self.model.add(Activation('sigmoid'))
            self.model.add(Dense(64, kernel_initializer="uniform"))
            self.model.add(Activation('sigmoid'))
            self.model.add(Dense(57, kernel_initializer="uniform"))
            self.model.add(Activation("sigmoid"))

            self.model.compile(loss='categorical_crossentropy',
                               optimizer='sgd',
                               metrics=["accuracy"])

    def add(self, s, a, r, sa):
        self.dataSet.append(TrainUnit(s, a, r, sa))

    def classify(self, s):
        s2 = numpy.array([s])
        q = self.model.predict(s2)
        return q.tolist()[0]

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
            self.before_save = 1000

        batch = self.get_batch(64)
        for unit in batch:
            s, a, r, sa = unit.disolve()
            qs = self.model.predict(numpy.array([s])).tolist()[0]
            qs[a] = r + self.LEARNING_FACTOR * (max(self.model.predict(numpy.array([sa])).tolist()[0]) - 0.5)
            self.model.fit(numpy.array([s]), numpy.array([qs]), epochs=1)
