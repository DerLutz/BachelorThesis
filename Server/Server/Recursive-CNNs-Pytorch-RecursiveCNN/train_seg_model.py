## Document Localization using Recursive CNN
## Maintainer : Khurram Javed
## Email : kjaved@ualberta.ca

''' Document Localization using Recursive CNN
 Maintainer : Khurram Javed
 Email : kjaved@ualberta.ca '''

from __future__ import print_function

import argparse

import torch
import torch.utils.data as td

import dataprocessor
import experiment as ex
import model
import trainer
import utils

parser = argparse.ArgumentParser(description='iCarl2.0')
parser.add_argument('--batch-size', type=int, default=32, metavar='N',
                    help='input batch size for training (default: 64)')
parser.add_argument('--lr', type=float, default=0.005, metavar='LR',
                    help='learning rate (default: 2.0)')
parser.add_argument('--schedule', type=int, nargs='+', default=[10, 20, 30],
                    help='Decrease learning rate at these epochs.')
parser.add_argument('--gammas', type=float, nargs='+', default=[0.2, 0.2, 0.2],
                    help='LR is multiplied by gamma on schedule, number of gammas should be equal to schedule')
parser.add_argument('--momentum', type=float, default=0.9, metavar='M',
                    help='SGD momentum (default: 0.9)')
parser.add_argument('--no-cuda', action='store_true', default=False,
                    help='disables CUDA training')
parser.add_argument('--pretrain', action='store_true', default=False,
                    help='Pretrain the model on CIFAR dataset?')
parser.add_argument('--load-ram', action='store_true', default=False,
                    help='Load data in ram')
parser.add_argument('--debug', action='store_true', default=True,
                    help='Debug messages')
parser.add_argument('--seed', type=int, default=2323,
                    help='Seeds values to be used')
parser.add_argument('--log-interval', type=int, default=5, metavar='N',
                    help='how many batches to wait before logging training status')
parser.add_argument('--model-type', default="resnet",
                    help='model type to be used. Example : resnet32, resnet20, densenet, test')
parser.add_argument('--name', default="noname",
                    help='Name of the experiment')
parser.add_argument('--output-dir', default="../",
                    help='Directory to store the results; a new folder "DDMMYYYY" will be created '
                         'in the specified directory to save the results.')
parser.add_argument('--decay', type=float, default=0.00001, help='Weight decay (L2 penalty).')
parser.add_argument('--epochs', type=int, default=40, help='Number of epochs for each increment')
parser.add_argument('--dataset', default="document", help='Dataset to be used; example CIFAR, MNIST')
parser.add_argument('--loader', default="hdd", help='Dataset to be used; example CIFAR, MNIST')
parser.add_argument("-i", "--data-dirs", nargs='+', default="/Users/khurramjaved96/documentTest64",
                    help="input Directory of train data")
parser.add_argument("-v", "--validation-dirs", nargs='+', default="/Users/khurramjaved96/documentTest64",
                    help="input Directory of val data")

args = parser.parse_args()

# Define an experiment.
my_experiment = ex.experiment(args.name, args, args.output_dir)

# Add logging support
logger = utils.utils.setup_logger(my_experiment.path)

args.cuda = not args.no_cuda and torch.cuda.is_available()

dataset = dataprocessor.DatasetFactory.get_dataset(args.data_dirs, args.dataset)

dataset_val = dataprocessor.DatasetFactory.get_dataset(args.validation_dirs, args.dataset)

# Fix the seed.
seed = args.seed
torch.manual_seed(seed)
if args.cuda:
    torch.cuda.manual_seed(seed)

train_dataset_loader = dataprocessor.LoaderFactory.get_loader(args.loader, dataset.myData,
                                                              transform=dataset.train_transform,
                                                              cuda=args.cuda)
# Loader used for training data
val_dataset_loader = dataprocessor.LoaderFactory.get_loader(args.loader, dataset_val.myData,
                                                            transform=dataset.test_transform,
                                                            cuda=args.cuda)
kwargs = {'num_workers': 1, 'pin_memory': True} if args.cuda else {}

# Iterator to iterate over training data.
train_iterator = torch.utils.data.DataLoader(train_dataset_loader,
                                             batch_size=args.batch_size, shuffle=True, **kwargs)
# Iterator to iterate over training data.
val_iterator = torch.utils.data.DataLoader(val_dataset_loader,
                                           batch_size=args.batch_size, shuffle=True, **kwargs)

# Get the required model
myModel = model.ModelFactory.get_model(args.model_type, args.dataset)
if args.cuda:
    myModel.cuda()

# Should I pretrain the model on CIFAR?
if args.pretrain:
    trainset = dataprocessor.DatasetFactory.get_dataset(None, "CIFAR")
    train_iterator_cifar = torch.utils.data.DataLoader(trainset, batch_size=32, shuffle=True, num_workers=2)

    # Define the optimizer used in the experiment
    cifar_optimizer = torch.optim.SGD(myModel.parameters(), args.lr, momentum=args.momentum,
                                      weight_decay=args.decay, nesterov=True)

    # Trainer object used for training
    cifar_trainer = trainer.CIFARTrainer(train_iterator_cifar, myModel, args.cuda, cifar_optimizer)

    for epoch in range(0, 70):
        logger.info("Epoch : %d", epoch)
        cifar_trainer.update_lr(epoch, [30, 45, 60], args.gammas)
        cifar_trainer.train(epoch)

    # Freeze the model
    counter = 0
    for name, param in myModel.named_parameters():
        # Getting the length of total layers so I can freeze x% of layers
        gen_len = sum(1 for _ in myModel.parameters())
        if counter < int(gen_len * 0.5):
            param.requires_grad = False
            logger.warning(name)
        else:
            logger.info(name)
        counter += 1

# Define the optimizer used in the experiment
optimizer = torch.optim.SGD(filter(lambda p: p.requires_grad, myModel.parameters()), args.lr,
                            momentum=args.momentum,
                            weight_decay=args.decay, nesterov=True)

# Trainer object used for training
my_trainer = trainer.Trainer(train_iterator, myModel, args.cuda, optimizer)

# Evaluator
my_eval = trainer.EvaluatorFactory.get_evaluator("rmse", args.cuda)
# Running epochs_class epochs
for epoch in range(0, args.epochs):
    logger.info("Epoch : %d", epoch)
    my_trainer.update_lr(epoch, args.schedule, args.gammas)
    my_trainer.train(epoch)
    my_eval.evaluate(my_trainer.model, val_iterator)

torch.save(myModel.state_dict(), my_experiment.path + args.dataset + "_" + args.model_type+ ".pb")
my_experiment.store_json()
